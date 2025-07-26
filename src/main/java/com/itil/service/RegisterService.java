package com.itil.service;

import com.itil.dto.ApiResponse;
import com.itil.dto.RegisterDTO;
import com.itil.dto.RegisterResponseDTO;
import com.itil.dto.UpdateUserInfoDTO;

public interface RegisterService {
   ApiResponse<RegisterResponseDTO> registerUser(RegisterDTO registerDTO);

   ApiResponse<UpdateUserInfoDTO> updateUser(Long id, UpdateUserInfoDTO updateUserInfoDTO);
}
