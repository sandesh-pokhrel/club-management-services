package com.fitness.clientservice.feign;

import com.fitness.clientservice.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@FeignClient(value = "auth-service", name = "auth-service")
public interface AuthFeignClient {

    @RequestMapping(method= RequestMethod.GET, value="/users/{username}")
    User getData(@PathVariable String username);
}
