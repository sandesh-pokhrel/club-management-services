package com.fitness.authservice.feign;

import com.fitness.authservice.model.Club;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@FeignClient(value = "client-service", name = "client-service")
public interface ClubFeignClient {

    @RequestMapping(method= RequestMethod.GET, value="/clubs/{id}")
    Club getClub(@PathVariable Integer id);
}
