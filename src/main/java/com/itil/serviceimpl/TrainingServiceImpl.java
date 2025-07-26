package com.itil.serviceimpl;

import com.itil.dto.*;
import com.itil.entity.Ticket;
import com.itil.entity.Training;
import com.itil.entity.User;
import com.itil.exception.ResourceNotFoundException;
import com.itil.repository.TicketRepository;
import com.itil.repository.TrainingRepository;
import com.itil.repository.UserRepository;
import com.itil.service.TrainingService;
import com.itil.util.ResponseUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingServiceImpl.class);

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ApiResponse<String> addTraining(TrainingDTO trainingDTO) {
        logger.info("Adding Training: " + trainingDTO.getTrainingName());
        Training training = modelMapper.map(trainingDTO, Training.class);
        trainingRepository.save(training);
        logger.info("Training: " + training.getTrainingName() + "added successfully");
        return ResponseUtil.success(training.getTrainingName(), "Training added successfully");
    }

    @Override
    public ApiResponse<String> assignTraining(Long ticketId, Long trainingId) {
        logger.info("Fetching ticket id for training assignment: " + ticketId);
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if(ticket.isEmpty()) {
            logger.error("Ticket not found with ID: " + ticketId);
            throw new ResourceNotFoundException("Ticket not found");
        }
        Optional<Training> training = trainingRepository.findById(trainingId);
        if(training.isEmpty()) {
            logger.error("Training not found with ID: " + trainingId);
            throw new ResourceNotFoundException("Training not found");
        }
        ticket.get().setTraining(training.get());
        ticketRepository.save(ticket.get());
        logger.info("Assigned: " + training.get().getTrainingName() + " training to ticket ID: " + ticketId);
        return ResponseUtil.success(training.get().getTrainingName(), "Training assigned successfully");
    }

    @Override
    public ApiResponse<TrainingResponseDTO> getTrainingDetails(Long trainingId) {
        logger.info("Fetching details for training ID: " + trainingId);
        Optional<Training> training = trainingRepository.findById(trainingId);
        if(training.isEmpty()) {
            logger.error("Training not found with ID: " + trainingId);
            throw  new ResourceNotFoundException("Training not found");
        }
        TrainingResponseDTO responseDTO = modelMapper.map(training, TrainingResponseDTO.class);
        logger.info("Successfully retrieved training details for training ID: " + trainingId);
        return ResponseUtil.success(responseDTO, "Training details fetched successfully");
    }

    @Override
    public ApiResponse<String> submitFeedback(Long trainingId, FeedbackDTO feedbackDTO) {
        logger.info("Submitting feedback for training ID: " + trainingId);
        Optional<Training> training = trainingRepository.findById(trainingId);
        if (training.isEmpty()) {
            logger.error("Training not found with ID: " + trainingId);
            throw  new ResourceNotFoundException("Training not found");
        }
        training.get().setFeedbacks(Collections.singletonList(feedbackDTO.getFeedback()));
        ArrayList<Double> ratings = new ArrayList<>();
        ratings.add(feedbackDTO.getRating());
        training.get().setRating(ratings);
        trainingRepository.save(training.get());
        logger.info("Feedback successfully submitted for training ID: " + trainingId);
        return ResponseUtil.success(training.get().getTrainingName(), "Feedback submitted successfully");
    }

    @Override
    public ApiResponse<TrainingFeedbackDTO> getTrainingFeedback(Long trainingId) {
        logger.info("Fetching feedback for training ID: " + trainingId);
        Optional<Training> training = trainingRepository.findById(trainingId);
        if (training.isEmpty()) {
            logger.error("Training not found with ID: " + trainingId);
            throw  new ResourceNotFoundException("Training not found");
        }
        TrainingFeedbackDTO responseDTO = modelMapper.map(training, TrainingFeedbackDTO.class);
        logger.info("Successfully retrieved feedback for training ID: " + trainingId);
        return ResponseUtil.success(responseDTO, "Feedback fetched successfully");
    }

    @Override
    public ApiResponse<String> issueCertificate(Long ticketId) {
        logger.info("Issuing certificate for ticket ID: ", ticketId);
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if(ticket.isEmpty()) {
            logger.error("Ticket not found with ID: " + ticketId);
            throw new ResourceNotFoundException("Ticket not found");
        }
        User user = ticket.get().getRaisedBy();
        if (user == null) {
            logger.error("User not found for ticket ID: " + ticketId);
            throw new ResourceNotFoundException("User not found for the ticket");
        }
        user.setCertificateName("Certificate for " + ticket.get().getTicketCategory() + " Training");
        user.setCertificateIssueDate(LocalDate.now());
        userRepository.save(user);
        logger.info("Certificate issued for user ID: " + user.getId() + " from ticket ID: " + ticketId);
        return ResponseUtil.success(user.getCertificateName(), "Certificate issued successfully");
    }
}
