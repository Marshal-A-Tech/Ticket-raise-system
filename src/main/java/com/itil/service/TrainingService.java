package com.itil.service;

import com.itil.dto.*;

public interface TrainingService {

    ApiResponse<String> addTraining(TrainingDTO trainingDTO);

    ApiResponse<String> assignTraining(Long ticketId, Long trainingId);

    ApiResponse<TrainingResponseDTO> getTrainingDetails(Long trainingId);

    ApiResponse<String> submitFeedback(Long trainingId, FeedbackDTO feedbackDTO);

    ApiResponse<TrainingFeedbackDTO> getTrainingFeedback(Long trainingId);

    ApiResponse<String> issueCertificate(Long ticketId);
}
