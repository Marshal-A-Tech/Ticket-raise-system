package com.itil.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class LoginRequest {
    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Invalid email format")
    private String username;
    @NotEmpty(message = "Password should not be empty")
    @Size(min=8, message = "Password must be at least 8 characters long")
    private String password;

    public LoginRequest(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
