package com.itil.service;


import com.itil.dto.ApiResponse;
import com.itil.dto.RegisterResponseDTO;
import com.itil.entity.User;

public interface UserService {
    ApiResponse<RegisterResponseDTO> getUserDetails(Long userId);
}
