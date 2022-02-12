package com.fitness.authservice.request;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChangeLogin implements Serializable {

    private String username;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
