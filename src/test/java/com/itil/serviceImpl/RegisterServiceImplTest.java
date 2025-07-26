package com.itil.serviceImpl;

import com.itil.dto.ApiResponse;
import com.itil.dto.RegisterDTO;
import com.itil.dto.RegisterResponseDTO;
import com.itil.dto.UpdateUserInfoDTO;
import com.itil.entity.Gatekeeper;
import com.itil.entity.TeamMember;
import com.itil.entity.User;
import com.itil.entity.UserInfo;
import com.itil.enums.Role;
import com.itil.enums.TicketCategory;
import com.itil.exception.EmailAlreadyExistsException;
import com.itil.exception.InvalidFormatException;
import com.itil.exception.ResourceNotFoundException;
import com.itil.repository.GatekeeperRepository;
import com.itil.repository.TeamMemberRepository;
import com.itil.repository.UserInfoRepository;
import com.itil.repository.UserRepository;
import com.itil.serviceimpl.RegisterServiceImpl;
import com.itil.util.ResponseUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterServiceImplTest {

    @InjectMocks
    private RegisterServiceImpl registerService;
    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private GatekeeperRepository gatekeeperRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ResponseUtil responseUtil;

    @Test
    void registerUser_ShouldThrowException_WhenEmailAlreadyExists() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("test@example.com");
        when(userInfoRepository.findByEmail(registerDTO.getEmail()))
                .thenReturn(Optional.of(new User()));
        assertThrows(EmailAlreadyExistsException.class, () -> {
            registerService.registerUser(registerDTO);
        });
        verify(userInfoRepository, times(1)).findByEmail(registerDTO.getEmail());
    }

    @Test
    void registerUser_ShouldRegisterUser_WhenRoleIsUser() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName("Ramu");
        registerDTO.setEmail("ramu@example.com");
        registerDTO.setPassword("password123");
        registerDTO.setRole(Role.ROLE_USER);
        User user = new User();
        user.setName(registerDTO.getName());
        user.setEmail(registerDTO.getEmail());
        RegisterResponseDTO responseDTO = new RegisterResponseDTO();
        responseDTO.setName(user.getName());
        responseDTO.setEmail(user.getEmail());
        when(userInfoRepository.findByEmail(registerDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, RegisterResponseDTO.class)).thenReturn(responseDTO);
        ApiResponse<RegisterResponseDTO> response = registerService.registerUser(registerDTO);
        assertNotNull(response);
        assertEquals("User details saved successfully", response.getMessage());
        assertEquals(registerDTO.getEmail(), response.getData().getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_ShouldRegisterTeamMember_WhenRoleIsTeamMember() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName("Arjun");
        registerDTO.setEmail("arjun@example.com");
        registerDTO.setPassword("securePass");
        registerDTO.setRole(Role.ROLE_TEAMMEMBER);
        registerDTO.setTicketCategory(TicketCategory.SOFTWARE);
        TeamMember teamMember = new TeamMember();
        teamMember.setName(registerDTO.getName());
        teamMember.setEmail(registerDTO.getEmail());
        teamMember.setTicketCategory(registerDTO.getTicketCategory());
        RegisterResponseDTO responseDTO = new RegisterResponseDTO();
        responseDTO.setName(teamMember.getName());
        responseDTO.setEmail(teamMember.getEmail());
        when(userInfoRepository.findByEmail(registerDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerDTO.getPassword())).thenReturn("encodedPassword");
        when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(teamMember);
        when(modelMapper.map(teamMember, RegisterResponseDTO.class)).thenReturn(responseDTO);
        ApiResponse<RegisterResponseDTO> response = registerService.registerUser(registerDTO);
        assertNotNull(response);
        assertEquals("User details saved successfully", response.getMessage());
        assertEquals(registerDTO.getEmail(), response.getData().getEmail());
        verify(teamMemberRepository, times(1)).save(any(TeamMember.class));
    }

    @Test
    void registerUser_ShouldRegisterGatekeeper_WhenRoleIsGatekeeper() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName("Harish");
        registerDTO.setEmail("harish@example.com");
        registerDTO.setPassword("pass1234");
        registerDTO.setRole(Role.ROLE_GATEKEEPER);
        Gatekeeper gatekeeper = new Gatekeeper();
        gatekeeper.setName(registerDTO.getName());
        gatekeeper.setEmail(registerDTO.getEmail());
        RegisterResponseDTO responseDTO = new RegisterResponseDTO();
        responseDTO.setName(gatekeeper.getName());
        responseDTO.setEmail(gatekeeper.getEmail());
        when(userInfoRepository.findByEmail(registerDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerDTO.getPassword())).thenReturn("encodedPassword");
        when(gatekeeperRepository.save(any(Gatekeeper.class))).thenReturn(gatekeeper);
        when(modelMapper.map(gatekeeper, RegisterResponseDTO.class)).thenReturn(responseDTO);
        ApiResponse<RegisterResponseDTO> response = registerService.registerUser(registerDTO);
        assertNotNull(response);
        assertEquals("User details saved successfully", response.getMessage());
        assertEquals(registerDTO.getEmail(), response.getData().getEmail());
        verify(gatekeeperRepository, times(1)).save(any(Gatekeeper.class));
    }

    @Test
    void updateUser_NotFoundException_WhenUserNotFound() {
        UpdateUserInfoDTO updateUserInfoDTO = new UpdateUserInfoDTO();
        updateUserInfoDTO.setName("New Name");
        updateUserInfoDTO.setEmail("newemail@example.com");
        updateUserInfoDTO.setPassword("newpassword");

        when(userInfoRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> registerService.updateUser(1L, updateUserInfoDTO));
        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userInfoRepository, times(1)).findById(any(Long.class));
    }

    @Test
    void updateUser_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
        UpdateUserInfoDTO updateUserInfoDTO = new UpdateUserInfoDTO();
        updateUserInfoDTO.setName("New Name");
        updateUserInfoDTO.setEmail("newemail@example.com");
        updateUserInfoDTO.setPassword("newpassword");

        when(userInfoRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> registerService.updateUser(1L, updateUserInfoDTO));
        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userInfoRepository, times(1)).findById(any(Long.class));
    }

    @Test
    void updateUser_ShouldUpdateName_WhenNameIsProvided() {
        UpdateUserInfoDTO updateUserInfoDTO = new UpdateUserInfoDTO();
        updateUserInfoDTO.setName("New Name");

        UserInfo existingUser = Mockito.mock(UserInfo.class);
        when(userInfoRepository.findById(any(Long.class))).thenReturn(Optional.of(existingUser));
        when(userInfoRepository.save(any(UserInfo.class))).thenReturn(existingUser);
        when(modelMapper.map(existingUser, UpdateUserInfoDTO.class)).thenReturn(updateUserInfoDTO);

        ApiResponse<UpdateUserInfoDTO> response = registerService.updateUser(1L, updateUserInfoDTO);
        assertNotNull(response);
        assertEquals("User updated successfully", response.getMessage());
        assertEquals("New Name", response.getData().getName());
        verify(userInfoRepository, times(1)).findById(any(Long.class));
        verify(userInfoRepository, times(1)).save(any(UserInfo.class));
    }

    @Test
    void updateUser_ShouldUpdateEmail_WhenEmailIsProvided() {
        UpdateUserInfoDTO updateUserInfoDTO = new UpdateUserInfoDTO();
        updateUserInfoDTO.setEmail("newemail@example.com");

        UserInfo existingUser = Mockito.mock(UserInfo.class);
        when(userInfoRepository.findById(any(Long.class))).thenReturn(Optional.of(existingUser));
        when(userInfoRepository.save(any(UserInfo.class))).thenReturn(existingUser);
        when(modelMapper.map(existingUser, UpdateUserInfoDTO.class)).thenReturn(updateUserInfoDTO);

        ApiResponse<UpdateUserInfoDTO> response = registerService.updateUser(1L, updateUserInfoDTO);
        assertNotNull(response);
        assertEquals("User updated successfully", response.getMessage());
        assertEquals("newemail@example.com", response.getData().getEmail());
        verify(userInfoRepository, times(1)).findById(any(Long.class));
        verify(userInfoRepository, times(1)).save(any(UserInfo.class));
    }

    @Test
    void updateUser_ShouldUpdatePassword_WhenPasswordIsProvided() {
        UpdateUserInfoDTO updateUserInfoDTO = new UpdateUserInfoDTO();
        updateUserInfoDTO.setPassword("encodedpassword");

        UserInfo existingUser = Mockito.mock(UserInfo.class);
        existingUser.setId(1L);
        existingUser.setName("Old Name");
        existingUser.setEmail("oldemail@example.com");
        existingUser.setPassword("oldpassword");

        when(userInfoRepository.findById(any(Long.class))).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedpassword");
        when(userInfoRepository.save(any(UserInfo.class))).thenReturn(existingUser);
        when(modelMapper.map(any(UserInfo.class), any(Class.class))).thenReturn(updateUserInfoDTO);

        ApiResponse<UpdateUserInfoDTO> response = registerService.updateUser(1L, updateUserInfoDTO);
        assertNotNull(response);
        assertEquals("User updated successfully", response.getMessage());
        assertEquals("encodedpassword", response.getData().getPassword());
        verify(userInfoRepository, times(1)).findById(any(Long.class));
        verify(userInfoRepository, times(1)).save(any(UserInfo.class));
    }



    @Test
    void updateUser_ShouldUpdateAllFields_WhenAllFieldsAreProvided() {
        UpdateUserInfoDTO updateUserInfoDTO = new UpdateUserInfoDTO();
        updateUserInfoDTO.setName("New Name");
        updateUserInfoDTO.setEmail("newemail@example.com");
        updateUserInfoDTO.setPassword("encodedpassword");

        UserInfo existingUser =  Mockito.mock(UserInfo.class);
        existingUser.setId(1L);
        existingUser.setName("Old Name");
        existingUser.setEmail("oldemail@example.com");
        existingUser.setPassword("oldpassword");

        when(userInfoRepository.findById(any(Long.class))).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedpassword");
        when(userInfoRepository.save(any(UserInfo.class))).thenReturn(existingUser);
        when(modelMapper.map(any(UserInfo.class), any(Class.class))).thenReturn(updateUserInfoDTO);

        ApiResponse<UpdateUserInfoDTO> response = registerService.updateUser(1L, updateUserInfoDTO);
        assertNotNull(response);
        assertEquals("User updated successfully", response.getMessage());
        assertEquals("New Name", response.getData().getName());
        assertEquals("newemail@example.com", response.getData().getEmail());
        assertEquals("encodedpassword", response.getData().getPassword());
        verify(userInfoRepository, times(1)).findById(any(Long.class));
        verify(userInfoRepository, times(1)).save(any(UserInfo.class));
    }

//    @Test
//    void testRegisterUser_InvalidRole() {
//        RegisterDTO registerDTO = new RegisterDTO();
//        registerDTO.setRole(null);
//
//        Exception exception = assertThrows(InvalidFormatException.class, () -> {
//            registerService.registerUser(registerDTO);
//        });
//        assert exception.getMessage().contains("Invalid role:");
//    }


}
