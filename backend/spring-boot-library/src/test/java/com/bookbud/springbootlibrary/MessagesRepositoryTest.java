package com.bookbud.springbootlibrary;

import com.bookbud.springbootlibrary.entity.Message;
import com.bookbud.springbootlibrary.repository.MessageRepository;
import com.bookbud.springbootlibrary.requestmodels.AdminQuestionRequest;
import com.bookbud.springbootlibrary.service.MessagesService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MessagesRepositoryTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessagesService messagesService;

    @Test
    @Order(1)
    @Rollback(value = false)
    public void postMessage_Success() {
        // Prepare test data
        String userEmail = "test@example.com";
        Message messageRequest = new Message("Test Title", "Test Question");

        // Mocking repository method
        when(messageRepository.save(any(Message.class))).thenReturn(messageRequest);

        // Execute the method to be tested
        messagesService.postMessage(messageRequest, userEmail);

        // Verify that the message is posted successfully
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    @Order(2)
    @Rollback(value = false)
    public void putMessage_Success() throws Exception {
        // Prepare test data
        String userEmail = "admin@example.com";
        AdminQuestionRequest adminQuestionRequest = new AdminQuestionRequest();
        adminQuestionRequest.setId(1L);
        adminQuestionRequest.setResponse("Test Response");

        Message existingMessage = new Message("Test Title", "Test Question");
        existingMessage.setId(1L);
        Optional<Message> optionalMessage = Optional.of(existingMessage);

        // Mocking repository method
        when(messageRepository.findById(adminQuestionRequest.getId())).thenReturn(optionalMessage);
        when(messageRepository.save(any(Message.class))).thenReturn(existingMessage);

        // Execute the method to be tested
        messagesService.putMessage(adminQuestionRequest, userEmail);

        // Verify that the message is updated successfully
        verify(messageRepository, times(1)).findById(adminQuestionRequest.getId());
        verify(messageRepository, times(1)).save(any(Message.class));
    }
}
