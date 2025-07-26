package com.itil.dto;

import com.itil.entity.Gatekeeper;
import com.itil.enums.Role;


public class TeamMemberResponseDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private int maxTicketsAllowed;
    private Gatekeeper gatekeeper;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getMaxTicketsAllowed() {
        return maxTicketsAllowed;
    }

    public void setMaxTicketsAllowed(int maxTicketsAllowed) {
        this.maxTicketsAllowed = maxTicketsAllowed;
    }

    public Gatekeeper getGatekeeper() {
        return gatekeeper;
    }

    public void setGatekeeper(Gatekeeper gatekeeper) {
        this.gatekeeper = gatekeeper;
    }
}
