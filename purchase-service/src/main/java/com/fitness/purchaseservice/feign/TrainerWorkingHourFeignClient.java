package com.fitness.purchaseservice.feign;

import com.fitness.purchaseservice.model.TrainerWorkingHour;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Component
@FeignClient(value = "auth-service", name = "auth-service")
public interface TrainerWorkingHourFeignClient {

    @RequestMapping(method= RequestMethod.GET, value="/trainer-working-hours/{username}")
    List<TrainerWorkingHour> getTrainerWorkingHour(@PathVariable String username);
}
