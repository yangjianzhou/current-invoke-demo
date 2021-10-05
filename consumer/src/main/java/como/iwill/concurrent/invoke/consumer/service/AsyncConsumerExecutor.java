package como.iwill.concurrent.invoke.consumer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AsyncConsumerExecutor {

    private Integer timeout = 100;

    private void checkInterrupted() {
        if (Thread.currentThread().isInterrupted()) {
            throw new IllegalStateException("thread is interrupted. dag will be aborted");
        }
    }

    private <T> T take(LinkedBlockingQueue<T> queue, Set<T> dones, Set<T> originItems) {
        try {

            checkInterrupted();
            T ret = queue.poll(timeout, TimeUnit.SECONDS);
            if (ret == null) {
                String uncompleted = originItems
                        .stream()
                        .filter(i -> !dones.contains(i))
                        .map(i -> i.getClass().getSimpleName())
                        .collect(Collectors.joining(","));

                log.error(String.format("these nodes(%s) didn't respond within %s seconds", uncompleted, timeout));

            }

            return ret;

        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public <T> void waitConsumeMultiItems(List<T> items, Consumer<T> consumer, Executor executor) {
        LinkedBlockingQueue<T> completedEventQueue = new LinkedBlockingQueue<>(items.size());
        Set<T> dones = new HashSet<>(items.size());
        Set<T> originItems = new HashSet<>(items);
        items.forEach(item ->
                CompletableFuture.runAsync(() -> consumer.accept(item), executor).whenComplete((aVoid, throwable) -> {
                    if (throwable != null) {
                        log.error(ExceptionUtils.getStackTrace(throwable));
                    }
                    completedEventQueue.add(item);
                })
        );
        T item;
        while (dones.size() < items.size()) {
            item = take(completedEventQueue, dones, originItems);
            if (item == null) {
                return;
            }
            dones.add(item);
        }
    }

    public <T> void waitConsumeMultiConsumers(T t, List<Consumer<T>> consumers, Executor executor) {
        LinkedBlockingQueue<Consumer> completedEventQueue = new LinkedBlockingQueue<>(consumers.size());
        Set<Consumer> dones = new HashSet<>(consumers.size());
        Set<Consumer> originItems = new HashSet<>(consumers);
        consumers.forEach(consumer ->
                CompletableFuture.runAsync(() -> consumer.accept(t), executor)
                        .whenComplete((aVoid, throwable) -> {
                            if (throwable != null) {
                                log.error(ExceptionUtils.getStackTrace(throwable));
                            }
                            completedEventQueue.add(consumer);
                        })
        );
        Consumer item;
        while (dones.size() < consumers.size()) {
            item = take(completedEventQueue, dones, originItems);
            if (item == null) {
                return;
            }
            dones.add(item);
        }
    }
}
