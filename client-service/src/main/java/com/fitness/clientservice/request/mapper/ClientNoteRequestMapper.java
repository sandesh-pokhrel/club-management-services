package com.fitness.clientservice.request.mapper;

import com.fitness.clientservice.feign.AuthFeignClient;
import com.fitness.clientservice.model.ClientNote;
import com.fitness.clientservice.request.ClientNoteRequest;
import com.fitness.clientservice.service.ClientService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Mapper(componentModel = "spring")
public interface ClientNoteRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client",
            expression = "java(clientService.getClientByUsername(clientNoteRequest.getClientUsername()))")
    @Mapping(target = "trainerUsername",
        expression = "java(authFeignClient.getData(clientNoteRequest.getTrainerUsername()).getUsername())")
    @Mapping(target = "createdDate", expression = "java(getDate())")
    ClientNote from(ClientNoteRequest clientNoteRequest, ClientService clientService,
                    AuthFeignClient authFeignClient);

    default Date getDate() {
        return new Date();
    }
}
