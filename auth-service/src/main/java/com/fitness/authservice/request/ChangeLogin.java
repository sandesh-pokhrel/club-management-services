package com.fitness.authservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
