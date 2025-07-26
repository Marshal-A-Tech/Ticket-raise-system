package com.itil.serviceimpl;

import com.itil.dto.ApiResponse;
import com.itil.dto.RegisterResponseDTO;
import com.itil.entity.User;
import com.itil.exception.ResourceNotFoundException;
import com.itil.repository.UserInfoRepository;
import com.itil.repository.UserRepository;
import com.itil.service.UserService;
import com.itil.util.ResponseUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ApiResponse<RegisterResponseDTO> getUserDetails(Long userId) {
        logger.info("Fetching user details for Id: " + userId);
        Optional<User> existUser = userRepository.findById(userId);
        if(existUser.isEmpty()) {
            logger.error("User not found with Id: " + userId);
            throw new ResourceNotFoundException("User not found with Id: " + userId);
        }
        RegisterResponseDTO userDTO = modelMapper.map(existUser.get(), RegisterResponseDTO.class);
        return ResponseUtil.success(userDTO, "User data fetched");
    }
}
