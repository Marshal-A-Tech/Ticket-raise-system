package com.itil.dto;


import com.itil.enums.Role;
import com.itil.enums.TicketCategory;
import jakarta.validation.constraints.*;

public class RegisterDTO {
    @NotEmpty(message = "Name should not be empty")
    @Pattern(regexp = "^[a-zA-Z ]*$",message = "Invalid name")
    private String name;
    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Invalid email format")
    private String email;
    @NotEmpty(message = "Password should not be empty")
    @Size(min=8, message = "Password must be at least 8 characters long")
    private String password;

    @NotNull(message = "Role should not be null")
    private Role role;

    private TicketCategory ticketCategory;

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public TicketCategory getTicketCategory() {
        return ticketCategory;
    }

    public void setTicketCategory(TicketCategory ticketCategory) {
        this.ticketCategory = ticketCategory;
    }
}
