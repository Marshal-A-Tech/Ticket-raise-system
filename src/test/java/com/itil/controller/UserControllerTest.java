package com.itil.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.itil.dto.ApiResponse;
import com.itil.dto.RegisterResponseDTO;
import com.itil.exception.GlobalExceptionHandler;
import com.itil.exception.ResourceNotFoundException;
import com.itil.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @MockBean
    private MockMvc mockMvc;

    private RegisterResponseDTO responseDTO;
    private ApiResponse<RegisterResponseDTO> response;

    @BeforeEach
    void setUp() {
        responseDTO = new RegisterResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Priya");
        responseDTO.setEmail("priya@example.com");
        response = new ApiResponse<>(true, "User data fetched", responseDTO, null, 0, System.currentTimeMillis());

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetUserDetails_Success() {
        when(userService.getUserDetails(1L)).thenReturn(response);
        ResponseEntity<ApiResponse<RegisterResponseDTO>> response = userController.getUserDetails(1L);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("User data fetched", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertEquals(1L, response.getBody().getData().getId());
        assertEquals("Priya", response.getBody().getData().getName());
        verify(userService, times(1)).getUserDetails(1L);
    }

}

