package com.itil.controller;

import com.itil.dto.ApiResponse;
import com.itil.dto.RegisterResponseDTO;
import com.itil.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<RegisterResponseDTO>> getUserDetails(@PathVariable Long userId) {
        logger.info("Fetching user with Id: " + userId);
        ApiResponse<RegisterResponseDTO> response = userService.getUserDetails(userId);
        logger.info("User fetched successfully with Id: " + userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
