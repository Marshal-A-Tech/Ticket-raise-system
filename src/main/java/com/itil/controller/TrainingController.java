package com.itil.controller;

import com.itil.dto.*;
import com.itil.service.TrainingService;
import com.itil.serviceimpl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private TrainingService trainingService;

    @PostMapping("/training")
    @PreAuthorize("hasAuthority('ROLE_GATEKEEPER')")
    public ResponseEntity<ApiResponse<String>> addTraining(@RequestBody TrainingDTO trainingDTO) {
        logger.info("Adding training: "+ trainingDTO.getTrainingName());
        ApiResponse<String> response = trainingService.addTraining(trainingDTO);
        logger.info("Training added successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("{trainingId}/assignments/{ticketId}")
    @PreAuthorize("hasAuthority('ROLE_GATEKEEPER')")
    public ResponseEntity<ApiResponse<String>> assignTraining(@PathVariable Long ticketId,
                                                              @PathVariable Long trainingId) {
        logger.info("Assigning training to ticket ID: "+ ticketId);
        ApiResponse<String> response = trainingService.assignTraining(ticketId, trainingId);
        logger.info("Training assigned successfully to ticket ID: "+ ticketId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{trainingId}")
    @PreAuthorize("hasAuthority('ROLE_GATEKEEPER')")
    public ResponseEntity<ApiResponse<TrainingResponseDTO>> getTrainingDetails(@PathVariable Long trainingId) {
        logger.info("Fetching training details for training ID: "+ trainingId);
        ApiResponse<TrainingResponseDTO> response = trainingService.getTrainingDetails(trainingId);
        logger.info("Successfully retrieved training details for training ID: "+ trainingId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{trainingId}/feedback")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse<String>> submitFeedback(@PathVariable Long trainingId,
                                                              @RequestBody FeedbackDTO feedbackDTO) {
        logger.info("Submitting feedback for training ID: "+ trainingId);
        ApiResponse<String> response = trainingService.submitFeedback(trainingId, feedbackDTO);
        logger.info("Feedback submitted successfully for training ID: "+ trainingId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{trainingId}/getFeedback")
    @PreAuthorize("hasAuthority('ROLE_GATEKEEPER')")
    public ResponseEntity<ApiResponse<TrainingFeedbackDTO>> getTrainingFeedback(@PathVariable Long trainingId) {
        logger.info("Fetching feedback for training ID: "+ trainingId);
        ApiResponse<TrainingFeedbackDTO> response = trainingService.getTrainingFeedback(trainingId);
        logger.info("Successfully retrieved feedback for training ID: "+ trainingId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/certificate/{ticketId}")
    @PreAuthorize("hasAuthority('ROLE_GATEKEEPER')")
    public ResponseEntity<ApiResponse<String>> issueCertificate(@PathVariable Long ticketId) {
        logger.info("Issuing certificate for ticket ID: "+ ticketId);
        ApiResponse<String> response = trainingService.issueCertificate(ticketId);
        logger.info("Certificate issued successfully for ticket ID: " +  ticketId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}