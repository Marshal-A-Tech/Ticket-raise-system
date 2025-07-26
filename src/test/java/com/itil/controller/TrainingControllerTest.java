package com.itil.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.itil.dto.*;
import com.itil.exception.GlobalExceptionHandler;
import com.itil.exception.ResourceNotFoundException;
import com.itil.service.TrainingService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class TrainingControllerTest {

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TrainingController trainingController;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(trainingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // Positive test cases

    @Test
    void testAddTraining_ValidRequest() throws Exception {
        TrainingDTO trainingDTO = new TrainingDTO();
        ApiResponse<String> response = new ApiResponse<>();
        response.setMessage("Training added successfully");

        when(trainingService.addTraining(any(TrainingDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/trainings/training")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(trainingDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Training added successfully"));
    }

    @Test
    void testAssignTraining_ValidRequest() throws Exception {
        ApiResponse<String> response = new ApiResponse<>();
        response.setMessage("Training assigned successfully");

        when(trainingService.assignTraining(any(Long.class), any(Long.class))).thenReturn(response);

        mockMvc.perform(post("/api/trainings/1/assignments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Training assigned successfully"));
    }

    @Test
    void testGetTrainingDetails_ValidTrainingId() throws Exception {
        TrainingResponseDTO trainingResponseDTO = new TrainingResponseDTO();
        ApiResponse<TrainingResponseDTO> response = new ApiResponse<>();
        response.setData(trainingResponseDTO);

        when(trainingService.getTrainingDetails(any(Long.class))).thenReturn(response);

        mockMvc.perform(get("/api/trainings/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testSubmitFeedback_ValidRequest() throws Exception {
        FeedbackDTO feedbackDTO = new FeedbackDTO();
        ApiResponse<String> response = new ApiResponse<>();
        response.setMessage("Feedback submitted successfully");

        when(trainingService.submitFeedback(any(Long.class), any(FeedbackDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/trainings/1/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(feedbackDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Feedback submitted successfully"));
    }

    @Test
    void testGetTrainingFeedback_ValidTrainingId() throws Exception {
        TrainingFeedbackDTO trainingFeedbackDTO = new TrainingFeedbackDTO();
        ApiResponse<TrainingFeedbackDTO> response = new ApiResponse<>();
        response.setData(trainingFeedbackDTO);

        when(trainingService.getTrainingFeedback(any(Long.class))).thenReturn(response);

        mockMvc.perform(get("/api/trainings/1/getFeedback")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testIssueCertificate_ValidTicketId() throws Exception {
        ApiResponse<String> response = new ApiResponse<>();
        response.setMessage("Certificate issued successfully");

        when(trainingService.issueCertificate(any(Long.class))).thenReturn(response);

        mockMvc.perform(post("/api/trainings/certificate/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Certificate issued successfully"));
    }

    @Test
    void testAddTraining_InvalidRequest() throws Exception {
        when(trainingService.addTraining(any(TrainingDTO.class)))
                .thenThrow(new ResourceNotFoundException("Invalid training details"));

        mockMvc.perform(post("/api/trainings/training")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"invalidField\":\"value\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void testAssignTraining_InvalidRequest() throws Exception {
        when(trainingService.assignTraining(any(Long.class), any(Long.class)))
                .thenThrow(new ResourceNotFoundException("Invalid ticket or training ID"));

        mockMvc.perform(post("/api/trainings/-1/assignments/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void testGetTrainingDetails_InvalidTrainingId() throws Exception {
        when(trainingService.getTrainingDetails(any(Long.class)))
                .thenThrow(new ResourceNotFoundException("Training not found"));

        mockMvc.perform(get("/api/trainings/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void testSubmitFeedback_InvalidRequest() throws Exception {
        when(trainingService.submitFeedback(any(Long.class), any(FeedbackDTO.class)))
                .thenThrow(new ResourceNotFoundException("Invalid feedback details"));

        mockMvc.perform(post("/api/trainings/-1/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"invalidField\":\"value\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void testGetTrainingFeedback_InvalidTrainingId() throws Exception {
        when(trainingService.getTrainingFeedback(any(Long.class)))
                .thenThrow(new ResourceNotFoundException("Training feedback not found"));

        mockMvc.perform(get("/api/trainings/-1/getFeedback")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void testIssueCertificate_InvalidTicketId() throws Exception {
        when(trainingService.issueCertificate(any(Long.class)))
                .thenThrow(new ResourceNotFoundException("Invalid ticket ID"));

        mockMvc.perform(post("/api/trainings/certificate/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }
}