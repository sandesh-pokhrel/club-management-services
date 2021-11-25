package com.fitness.clientservice.request.mapper;

import com.fitness.clientservice.model.ClientNote;
import com.fitness.clientservice.repository.UserRepository;
import com.fitness.clientservice.request.ClientNoteRequest;
import com.fitness.clientservice.service.ClientService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ClientNoteRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client",
            expression = "java(clientService.getClientByUsername(clientNoteRequest.getClientUsername()))")
    @Mapping(target = "user",
            expression = "java(userRepository.findById(clientNoteRequest.getTrainerUsername()).orElse(null))")
    @Mapping(target = "createdDate", expression = "java(getDate())")
    ClientNote from(ClientNoteRequest clientNoteRequest, ClientService clientService,
                    UserRepository userRepository);

    default Date getDate() {
        return new Date();
    }
}
