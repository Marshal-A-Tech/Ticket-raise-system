package com.itil.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateUserInfoDTO {

    @Pattern(regexp = "^[a-zA-Z ]*$",message = "Invalid name")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min=8, message = "Password must be at least 8 characters long")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
