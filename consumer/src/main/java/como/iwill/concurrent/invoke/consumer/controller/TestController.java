package como.iwill.concurrent.invoke.consumer.controller;

import como.iwill.concurrent.invoke.consumer.UserDTO;
import como.iwill.concurrent.invoke.consumer.service.AsyncConsumerExecutor;
import como.iwill.concurrent.invoke.consumer.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    private ProviderService providerService;

    @Autowired
    private AsyncConsumerExecutor asyncConsumerExecutor;

    private static ExecutorService executor = new ThreadPoolExecutor(12, 12, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    @GetMapping("get-consumer-info1")
    public String getConsumerInfo1() {
        long startTime = System.currentTimeMillis();
        UserDTO userDemoDTO = new UserDTO();
        List<Consumer<UserDTO>> consumers = new ArrayList<>();
        consumers.add(userDTO -> providerService.decorateName(userDTO));
        consumers.add(userDTO -> providerService.decorateAddress(userDTO));
        consumers.add(userDTO -> providerService.decorateAge(userDTO));
        consumers.add(userDTO -> providerService.decorateMobile(userDTO));
        asyncConsumerExecutor.waitConsumeMultiConsumers(userDemoDTO, consumers, executor);
        long endTime =  System.currentTimeMillis();
        System.out.println(userDemoDTO +":"+ ( endTime  - startTime));
        return "success";
    }

    @GetMapping("get-consumer-info2")
    public String getConsumerInfo2() {
        long startTime = System.currentTimeMillis();
        UserDTO userDTO = new UserDTO();

        providerService.decorateName(userDTO);
        providerService.decorateAddress(userDTO);
        providerService.decorateAge(userDTO);
        providerService.decorateMobile(userDTO);
        // asyncConsumerExecutor.waitConsumeMultiConsumers(userDemoDTO ,consumers ,executor);
        long endTime =  System.currentTimeMillis();
        System.out.println(userDTO +":"+ ( endTime  - startTime));
        return "success";
    }
}
