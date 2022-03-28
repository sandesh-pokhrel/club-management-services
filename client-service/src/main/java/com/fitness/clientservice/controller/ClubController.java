package com.fitness.clientservice.controller;

import com.fitness.clientservice.model.Club;
import com.fitness.clientservice.service.ClubService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clubs")
@AllArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Club> getAll() {
        return this.clubService.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Club getById(@PathVariable Integer id) {
        return this.clubService.getById(id);
    }
}
