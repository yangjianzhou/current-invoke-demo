package como.iwill.concurrent.invoke.consumer.service;

import como.iwill.concurrent.invoke.consumer.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProviderService {

    @Autowired
    private RestTemplate restTemplate;

    public void decorateName(UserDTO userDTO) {
        String result = restTemplate.getForObject("http://localhost:8090/test/get-name", String.class);
        if (StringUtils.isNotBlank(result)) {
            userDTO.setName(result);
        }
    }

    public void decorateAddress(UserDTO userDTO) {
        String result = restTemplate.getForObject("http://localhost:8090/test/get-address", String.class);
        if (StringUtils.isNotBlank(result)) {
            userDTO.setAddress(result);
        }
    }

    public void decorateAge(UserDTO userDTO) {
        Integer result = restTemplate.getForObject("http://localhost:8090/test/get-age", Integer.class);
        if (result != null) {
            userDTO.setAge(result);
        }
    }

    public void decorateMobile(UserDTO userDTO) {
        String result = restTemplate.getForObject("http://localhost:8090/test/get-mobile", String.class);
        if (result != null) {
            userDTO.setMobile(result);
        }
    }
}
