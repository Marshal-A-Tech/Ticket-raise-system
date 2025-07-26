package com.itil.serviceImpl;

import com.itil.dto.ApiResponse;
import com.itil.dto.FeedbackDTO;
import com.itil.dto.TrainingDTO;
import com.itil.dto.TrainingResponseDTO;
import com.itil.entity.Ticket;
import com.itil.entity.Training;
import com.itil.entity.User;
import com.itil.enums.TicketCategory;
import com.itil.exception.ResourceNotFoundException;
import com.itil.repository.TicketRepository;
import com.itil.repository.TrainingRepository;
import com.itil.repository.UserRepository;
import com.itil.serviceimpl.TrainingServiceImpl;
import com.itil.util.ResponseUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceImplTest {

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Logger logger;

    public TrainingServiceImplTest() {
    }

    @Test
    void addTraining_ShouldReturnSuccessMessage_WhenTrainingAdded() {
        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setTrainingName("Java Training");
        Training training = new Training();
        training.setTrainingName("Java Training");
        when(modelMapper.map(trainingDTO, Training.class)).thenReturn(training);
        when(trainingRepository.save(training)).thenReturn(training);
        ApiResponse<String> response = trainingService.addTraining(trainingDTO);
        assertTrue(response.isSuccess());
        assertEquals("Java Training", response.getData());
        assertEquals("Training added successfully", response.getMessage());
        verify(trainingRepository, times(1)).save(training);
    }

    @Test
    void testAssignTraining_Success() {
        Long ticketId = 1L;
        Long trainingId = 1L;

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);

        Training training = new Training();
        training.setId(trainingId);
        training.setTrainingName("Java Training");

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        ApiResponse<String> response = trainingService.assignTraining(ticketId, trainingId);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Training assigned successfully", response.getMessage());
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(trainingRepository, times(1)).findById(trainingId);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }
    @Test
    void testAssignTraining_TicketNotFound() {

        Long ticketId = 1L;
        Long trainingId = 1L;

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            trainingService.assignTraining(ticketId, trainingId);
        });

        assertEquals("Ticket not found", exception.getMessage());
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(trainingRepository, never()).findById(any(Long.class));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void testAssignTraining_TrainingNotFound() {
        Long ticketId = 1L;
        Long trainingId = 1L;

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            trainingService.assignTraining(ticketId, trainingId);
        });

        assertEquals("Training not found", exception.getMessage());
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(trainingRepository, times(1)).findById(trainingId);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }


    @Test
    void submitFeedback_ShouldSaveFeedback_WhenTrainingExists() {
        Long trainingId = 1L;
        FeedbackDTO feedbackDTO = new FeedbackDTO();
        feedbackDTO.setFeedback("Great training!");
        feedbackDTO.setRating(4.7);
        Training training = new Training();
        training.setId(trainingId);
        training.setFeedbacks(new ArrayList<>());
        training.setRating(new ArrayList<>());
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));
        ApiResponse<String> response = trainingService.submitFeedback(trainingId, feedbackDTO);
        assertTrue(response.isSuccess());
        assertEquals("Feedback submitted successfully", response.getMessage());
        assertTrue(training.getFeedbacks().contains("Great training!"));
        assertTrue(training.getRating().contains(4.7));
        verify(trainingRepository, times(1)).save(training);
    }

    @Test
    void submitFeedback_ShouldThrowException_WhenTrainingNotFound() {
        Long trainingId = 1L;
        FeedbackDTO feedbackDTO = new FeedbackDTO();
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            trainingService.submitFeedback(trainingId, feedbackDTO);
        });
        assertEquals("Training not found", exception.getMessage());
    }

//    @Test
//    void getTrainingFeedback_ShouldReturnFeedback_WhenTrainingExists() {
//        Long trainingId = 1L;
//        Training training = new Training();
//        training.setId(trainingId);
//        training.setFeedbacks(Arrays.asList("Excellent", "Very Informative"));
//        TrainingFeedbackDTO responseDTO = new TrainingFeedbackDTO();
//        responseDTO.setFeedbacks(training.getFeedbacks());
//        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));
//        when(modelMapper.map(training, TrainingFeedbackDTO.class)).thenReturn(responseDTO);
//        ApiResponse<TrainingFeedbackDTO> response = trainingService.getTrainingFeedback(trainingId);
//        assertTrue(response.isSuccess());
//        assertEquals("Feedback fetched successfully", response.getMessage());
//        assertEquals(2, response.getData().getFeedbacks().size());
//    }

    @Test
    void issueCertificate_ShouldIssueCertificate_WhenTicketAndUserExist() {
        Long ticketId = 1L;
        User user = new User();
        user.setId(2L);
        user.setName("John Doe");
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setTicketCategory(TicketCategory.SOFTWARE);
        ticket.setRaisedBy(user);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(userRepository.save(user)).thenReturn(user);
        ApiResponse<String> response = trainingService.issueCertificate(ticketId);
        assertTrue(response.isSuccess());
        assertEquals("Certificate for SOFTWARE Training", response.getData());
        assertEquals("Certificate issued successfully", response.getMessage());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void issueCertificate_ShouldThrowException_WhenTicketNotFound() {
        Long ticketId = 1L;
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            trainingService.issueCertificate(ticketId);
        });
        assertEquals("Ticket not found", exception.getMessage());
    }

    @Test
    void issueCertificate_ShouldThrowException_WhenUserNotFound() {
        Long ticketId = 1L;
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setRaisedBy(null);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            trainingService.issueCertificate(ticketId);
        });
        assertEquals("User not found for the ticket", exception.getMessage());
    }

//    @Test
//    void testGetTrainingDetails_Success() {
//        Long trainingId = 1L;
//        Training mockTraining = new Training();
//        mockTraining.setId(trainingId);
//        mockTraining.setTrainingName("Java Training");
//        mockTraining.setTrainer("Ganesh");
//
//        TrainingResponseDTO responseDTO = new TrainingResponseDTO();
//        responseDTO.setTrainingName("Java Training");
//        responseDTO.setTrainer("Ganesh");
//
//        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(mockTraining));
//        when(modelMapper.map(mockTraining, TrainingResponseDTO.class)).thenReturn(responseDTO);
//
//        ApiResponse<TrainingResponseDTO> response = trainingService.getTrainingDetails(trainingId);
//
//        assertNotNull(response);
//        assertEquals("Training details fetched successfully", response.getMessage());
//        assertEquals("Java Training", response.getData().getTrainingName());
//
//        verify(trainingRepository, times(1)).findById(trainingId);
//        verify(modelMapper, times(1)).map(any(Training.class), TrainingResponseDTO.class);
//    }

    @Test
    void testGetTrainingDetails_TrainingNotFound() {
        Long trainingId = 999L;
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> trainingService.getTrainingDetails(trainingId));

        assertEquals("Training not found", exception.getMessage());

        verify(trainingRepository, times(1)).findById(trainingId);
        verifyNoInteractions(modelMapper);
    }
}
