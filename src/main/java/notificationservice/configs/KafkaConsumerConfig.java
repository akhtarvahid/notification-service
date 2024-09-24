package notificationservice.configs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import notificationservice.dtos.SendEmailMessageDto;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import javax.mail.Session;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.Properties;

@Configuration
public class KafkaConsumerConfig {
    private ObjectMapper objectMapper;
    private EmailUtil emailUtil;

    public KafkaConsumerConfig(ObjectMapper objectMapper, EmailUtil emailUtil) {
        this.objectMapper = objectMapper;
        this.emailUtil = emailUtil;
    }

    // Listen an event from Kafka publisher
    @KafkaListener(topics = "sendEmail", groupId = "notificationService")
    public void handleSendEmailEvent(String message){
        SendEmailMessageDto emailMessageDto;
        // Send an email to user
        try {
             emailMessageDto = objectMapper.readValue(message, SendEmailMessageDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                // generate the password from https://myaccount.google.com/apppasswords for your from_account entering service name
                return new PasswordAuthentication("akhtar.v00@gmail.com", "jxlxnmhbuixdnrmk");
            }
        };
        Session session = Session.getInstance(props, auth);

        emailUtil.sendEmail(session, emailMessageDto.getTo(),emailMessageDto.getSubject(), emailMessageDto.getBody());

        System.out.println("Received request in handleSendEmailEvent");
    }
}
