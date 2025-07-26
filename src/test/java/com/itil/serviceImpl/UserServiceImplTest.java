package com.itil.serviceImpl;

import com.itil.dto.ApiResponse;
import com.itil.dto.RegisterResponseDTO;
import com.itil.entity.User;
import com.itil.exception.ResourceNotFoundException;
import com.itil.repository.UserRepository;
import com.itil.serviceimpl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void getUserDetails_ShouldReturnUserDetails_WhenUserExists() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        RegisterResponseDTO responseDTO = new RegisterResponseDTO();
        responseDTO.setName(user.getName());
        responseDTO.setEmail(user.getEmail());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, RegisterResponseDTO.class)).thenReturn(responseDTO);
        ApiResponse<RegisterResponseDTO> response = userService.getUserDetails(userId);
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("User data fetched", response.getMessage());
        assertEquals(user.getEmail(), response.getData().getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(modelMapper, times(1)).map(user, RegisterResponseDTO.class);
    }

    @Test
    void getUserDetails_ShouldThrowException_WhenUserNotFound() {
        Long userId = 19L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserDetails(userId);
        });
        assertEquals("User not found with Id: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(modelMapper);
    }
}
