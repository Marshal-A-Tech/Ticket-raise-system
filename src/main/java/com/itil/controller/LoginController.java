package com.itil.controller;

import com.itil.dto.ApiResponse;
import com.itil.dto.LoginRequest;
import com.itil.service.JwtService;
import com.itil.service.UserInfoService;
import com.itil.util.ResponseUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);


    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> authenticateAndGetToken(
            @RequestBody @Valid LoginRequest loginRequest) {
        try {
            logger.info("Authenticating user: " + loginRequest.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword())
            );
            String token = jwtService.generateToken(loginRequest.getUsername());
            logger.info("Token generated successfully for user: "+ loginRequest.getUsername());
            return ResponseEntity.ok(ResponseUtil.success(token, "Token generated successfully"));
        } catch (BadCredentialsException ex) {
            logger.error("Invalid login attempt for user: "+ loginRequest.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
