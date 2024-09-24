package notificationservice.configs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import notificationservice.dtos.SendEmailMessageDto;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
public class KafkaConsumerConfig {
    private ObjectMapper objectMapper;
    public KafkaConsumerConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Listen an event from Kafka publisher
    @KafkaListener(topics = "sendEmail", groupId = "notificationService")
    public void handleSendEmailEvent(String message){
        // Send an email to user
        try {
            SendEmailMessageDto emailMessageDto = objectMapper.readValue(message, SendEmailMessageDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Received request in handleSendEmailEvent");
    }
}
