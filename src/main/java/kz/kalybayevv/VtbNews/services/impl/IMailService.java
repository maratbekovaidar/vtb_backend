package kz.kalybayevv.VtbNews.services.impl;

import kz.kalybayevv.VtbNews.constants.Constants;
import kz.kalybayevv.VtbNews.services.MailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * Service for sending emails.
 * <p>
 * We use the @Async annotation to send emails asynchronously.
 */
@Service
@RequiredArgsConstructor
public class IMailService implements MailService {
    private final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender javaMailSender;

    @Async
    @Override
    public Future<Boolean> sendEmail(String to, String subject, String content) {
        if (log.isDebugEnabled()) {
            log.debug("Send email to '{}' with subject '{}' and content={}", to, subject, content);
        } else {
            log.info("Send email to '{}' with subject '{}'", to, subject);
        }
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        try {
            simpleMailMessage.setTo(to);
            simpleMailMessage.setFrom(Constants.JAVA_EMAIL_SENDER);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(content);
            javaMailSender.send(simpleMailMessage);
            log.info("Sent email to User '{}'", to);
            return AsyncResult.forValue(true);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.warn("Email could not be sent to user '{}'", to, e);
            } else {
                log.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
            }
        }
        return AsyncResult.forValue(false);
    }
}
