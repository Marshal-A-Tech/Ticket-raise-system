package com.itil.controller;

import com.itil.dto.ApiResponse;
import com.itil.dto.RegisterDTO;
import com.itil.dto.RegisterResponseDTO;
import com.itil.dto.UpdateUserInfoDTO;
import com.itil.service.RegisterService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class RegisterController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private RegisterService registerService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponseDTO>> registerUser(@Valid @RequestBody RegisterDTO registerDTO) {
        logger.info("Registering user with email: " + registerDTO.getEmail());
        ApiResponse<RegisterResponseDTO> response = registerService.registerUser(registerDTO);
        logger.info("User registered successfully: " + response.getData().getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_GATEKEEPER', 'ROLE_TEAMMEMBER', 'ROLE_USER')")
    public ResponseEntity<ApiResponse<UpdateUserInfoDTO>> updateUser(@PathVariable Long id,
            @Valid @RequestBody UpdateUserInfoDTO updateUserInfoDTO) {
        logger.info("Update user with Id: " + updateUserInfoDTO.getEmail());
        ApiResponse<UpdateUserInfoDTO> response = registerService.updateUser(id, updateUserInfoDTO);
        logger.info("User updated successfully with Id: " + id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
