package com.itil.serviceimpl;

import com.itil.dto.ApiResponse;
import com.itil.dto.RegisterDTO;
import com.itil.dto.RegisterResponseDTO;
import com.itil.dto.UpdateUserInfoDTO;
import com.itil.entity.*;
import com.itil.exception.BadRequestException;
import com.itil.exception.EmailAlreadyExistsException;
import com.itil.exception.InvalidFormatException;
import com.itil.exception.ResourceNotFoundException;
import com.itil.repository.GatekeeperRepository;
import com.itil.repository.TeamMemberRepository;
import com.itil.repository.UserInfoRepository;
import com.itil.repository.UserRepository;
import com.itil.service.RegisterService;
import com.itil.util.ResponseUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegisterServiceImpl implements RegisterService {

    private static final Logger logger = LoggerFactory.getLogger(RegisterServiceImpl.class);

    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GatekeeperRepository gatekeeperRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ApiResponse<RegisterResponseDTO> registerUser(RegisterDTO registerDTO) {
        Optional<UserInfo> existingUser = userInfoRepository.findByEmail(registerDTO.getEmail());
        if(existingUser.isPresent()){
            logger.error("Email is already registered: " + registerDTO.getEmail());
            throw new EmailAlreadyExistsException("Email is already registered: " + registerDTO.getEmail());
        }
        UserInfo newUser;
        switch (registerDTO.getRole()) {
            case ROLE_USER:
                newUser = new User();
                break;
            case ROLE_GATEKEEPER:
                newUser = new Gatekeeper();
                break;
            case ROLE_TEAMMEMBER:
                newUser = new TeamMember();
                ((TeamMember) newUser).setTicketCategory(registerDTO.getTicketCategory());
                break;
            default:
                logger.error("Invalid role: ", registerDTO.getRole());
                throw new InvalidFormatException("Invalid role: " + registerDTO.getRole());
        }
        newUser.setName(registerDTO.getName());
        newUser.setEmail(registerDTO.getEmail());
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        newUser.setPassword(encodedPassword);
        newUser.setRole(registerDTO.getRole());

        UserInfo savedUser;

        if (newUser instanceof User) {
            savedUser = userRepository.save((User) newUser);
        } else if (newUser instanceof Gatekeeper) {
            savedUser = gatekeeperRepository.save((Gatekeeper) newUser);
        } else {
           savedUser = teamMemberRepository.save((TeamMember) newUser);
        }
        RegisterResponseDTO userDTO = modelMapper.map(savedUser, RegisterResponseDTO.class);
        return ResponseUtil.success(userDTO, "User details saved successfully");
    }

    @Override
    public ApiResponse<UpdateUserInfoDTO> updateUser(Long id, UpdateUserInfoDTO updateUserInfoDTO) {
        logger.info("Fetching details for Id: " + id);
        Optional<UserInfo> existingUserOpt = userInfoRepository.findById(id);
        if (existingUserOpt.isEmpty()) {
            logger.error("User not found with id: " + id);
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        UserInfo existingUser = existingUserOpt.get();
        if (updateUserInfoDTO.getName() != null) {
            existingUser.setName(updateUserInfoDTO.getName());
        }
        if (updateUserInfoDTO.getEmail() != null) {
            existingUser.setEmail(updateUserInfoDTO.getEmail());
        }
        if (updateUserInfoDTO.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(updateUserInfoDTO.getPassword()));
        }
        UserInfo updatedUser = userInfoRepository.save(existingUser);
        UpdateUserInfoDTO updateUser = modelMapper.map(updatedUser, UpdateUserInfoDTO.class);
        return ResponseUtil.success(updateUser, "User updated successfully");
    }
}

