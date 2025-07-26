package com.itil.controller;

import com.itil.dto.*;
import com.itil.enums.Role;
import com.itil.service.RegisterService;
import com.itil.util.ResponseUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterControllerTest {

    @Mock
    private RegisterService registerService;

    @InjectMocks
    private RegisterController registerController;

    private RegisterDTO registerDTO;
    private RegisterResponseDTO registerResponseDTO;

    private UpdateUserInfoDTO updateUserInfoDTO;

    @BeforeEach
    void setUp() {
        registerDTO = new RegisterDTO();
        registerDTO.setName("Priya");
        registerDTO.setEmail("priya@example.com");
        registerDTO.setPassword("password123");
        registerDTO.setRole(Role.ROLE_USER);

        registerResponseDTO = new RegisterResponseDTO();
        registerResponseDTO.setId(1L);
        registerResponseDTO.setName(registerDTO.getName());
        registerResponseDTO.setEmail(registerDTO.getEmail());
        registerResponseDTO.setRole(registerDTO.getRole());

        updateUserInfoDTO = new UpdateUserInfoDTO();
        updateUserInfoDTO.setEmail("test@example.com");
        updateUserInfoDTO.setName("Test User");
    }

    @Test
    void testRegisterUser_Success() {
        ApiResponse<RegisterResponseDTO> mockResponse = ResponseUtil.success(registerResponseDTO, "User registered successfully");
        when(registerService.registerUser(any(RegisterDTO.class))).thenReturn(mockResponse);
        ResponseEntity<ApiResponse<RegisterResponseDTO>> response = registerController.registerUser(registerDTO);
        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("User registered successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertEquals(1L, response.getBody().getData().getId());
        assertEquals("Priya", response.getBody().getData().getName());
        verify(registerService, times(1)).registerUser(any(RegisterDTO.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        when(registerService.registerUser(any(RegisterDTO.class)))
                .thenThrow(new RuntimeException("Email is already registered"));
        Exception exception = assertThrows(RuntimeException.class, () -> registerController.registerUser(registerDTO));
        assertEquals("Email is already registered", exception.getMessage());
        verify(registerService, times(1)).registerUser(any(RegisterDTO.class));
    }

    @Test
    void testUpdateUser_Success() {
        ApiResponse<UpdateUserInfoDTO> mockResponse = ResponseUtil.success(updateUserInfoDTO,
                "User updated successfully");
        when(registerService.updateUser(any(Long.class), any(UpdateUserInfoDTO.class))).thenReturn(mockResponse);
        ResponseEntity<ApiResponse<UpdateUserInfoDTO>> response = registerController.updateUser(1L, updateUserInfoDTO);
        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("User updated successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertEquals("test@example.com", response.getBody().getData().getEmail());
        verify(registerService, times(1)).updateUser(any(Long.class), any(UpdateUserInfoDTO.class));
    }

    @Test
    void testUpdateUser_InvalidUserId() {
        when(registerService.updateUser(any(Long.class), any(UpdateUserInfoDTO.class)))
                .thenThrow(new RuntimeException("User not found"));
        Exception exception = assertThrows(RuntimeException.class,
                () -> registerController.updateUser(9999L, updateUserInfoDTO));
        assertEquals("User not found", exception.getMessage());
        verify(registerService, times(1)).updateUser(any(Long.class), any(UpdateUserInfoDTO.class));
    }

    @Test
    void testUpdateUser_InvalidUpdateUserInfoDTO() {
        UpdateUserInfoDTO invalidDTO = new UpdateUserInfoDTO();
        when(registerService.updateUser(any(Long.class), any(UpdateUserInfoDTO.class)))
                .thenThrow(new RuntimeException("Invalid user info"));
        Exception exception = assertThrows(RuntimeException.class, () -> registerController.updateUser(1L, invalidDTO));
        assertEquals("Invalid user info", exception.getMessage());
        verify(registerService, times(1)).updateUser(any(Long.class), any(UpdateUserInfoDTO.class));
    }

    @Test
    void testUpdateUser_UnauthorizedAccess() {
        when(registerService.updateUser(any(Long.class), any(UpdateUserInfoDTO.class)))
                .thenThrow(new RuntimeException("Access denied"));
        Exception exception = assertThrows(RuntimeException.class,
                () -> registerController.updateUser(1L, updateUserInfoDTO));
        assertEquals("Access denied", exception.getMessage());
        verify(registerService, times(1)).updateUser(any(Long.class), any(UpdateUserInfoDTO.class));
    }

    @Test
    void testUpdateUser_EmptyRequestBody() {
        UpdateUserInfoDTO emptyDTO = new UpdateUserInfoDTO();
        when(registerService.updateUser(any(Long.class), any(UpdateUserInfoDTO.class)))
                .thenThrow(new RuntimeException("Invalid user info"));
        Exception exception = assertThrows(RuntimeException.class, () -> registerController.updateUser(1L, emptyDTO));
        assertEquals("Invalid user info", exception.getMessage());
        verify(registerService, times(1)).updateUser(any(Long.class), any(UpdateUserInfoDTO.class));
    }
}
