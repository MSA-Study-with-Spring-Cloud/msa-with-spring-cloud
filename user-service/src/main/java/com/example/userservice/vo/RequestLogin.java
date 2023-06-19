package com.example.userservice.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestLogin {
    @NotNull(message = "Email cannot be null")
    @Size(message = "Email not be less than two characters", min = 2)
    private String  email;

    @NotNull(message = "Password cannot be null")
    @Size(message = "Password must be equals or greater than 8 characters", min = 8)
    private String password;

}
