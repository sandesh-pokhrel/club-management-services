package com.fitness.sharedapp.util;

import com.fitness.sharedapp.common.MailProperties;
import com.fitness.sharedapp.common.MailType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

@Component
@RequiredArgsConstructor
public class MailUtil {
    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;

    @Value("${clubsystem.frontend.url}")
    private String frontendURL;

    @Value("${mail.questionnaire}")
    private String questionnaireMailFormat;

    private MimeMessage getMessageFormat(String toEmail, MailType mailType, String serial,
                                         byte[] attachmentBytes) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
        helper.setFrom(mailProperties.getUsername());
        helper.setTo(toEmail);

        if (mailType == MailType.CLIENT_QUESTIONNAIRE) {
            questionnaireMailFormat = questionnaireMailFormat.replace("{##frontEndUrL##}", frontendURL+serial);
            helper.setSubject("Club Management Questionnaire");
            helper.setText(questionnaireMailFormat, true);
        } else if (mailType == MailType.RESET_PASSWORD) {
            System.out.println("Reset password request");
        } else if (mailType == MailType.CLIENT_ASSESSMENT) {
            helper.setSubject("Club Management Client Assessment");
            String bodyText = "<h2>Record Assessment</h2>";
            bodyText += "<h3>The attachment contains your record assessment. Please review it and analyze your progress.</h3>";
            helper.setText(bodyText, true);
            String fileName = "assessment.pdf";
            helper.addAttachment(fileName, new ByteArrayDataSource(attachmentBytes, "application/octet-stream"));

        }

        return message;
    }

    @Async("threadPoolTaskExecutor")
    public void sendMail(String toEmail, MailType mailType, String serialNumber, byte[] attachmentBytes)
            throws MessagingException {
        MimeMessage message = getMessageFormat(toEmail, mailType, serialNumber, attachmentBytes);
        javaMailSender.send(message);
    }
}
