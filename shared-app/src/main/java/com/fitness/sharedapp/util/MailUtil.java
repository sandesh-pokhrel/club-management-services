package com.fitness.sharedapp.util;

import com.fitness.sharedapp.common.MailProperties;
import com.fitness.sharedapp.common.MailType;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

@Component
@AllArgsConstructor
public class MailUtil {
    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;

    private MimeMessage getMessageFormat(String toEmail, MailType mailType, String serial,
                                         byte[] attachmentBytes) throws MessagingException {
        String buttonClass = "background-color: #EEEEEE;" +
                "border: 1px solid black;" +
                "color: green;" +
                "padding: 5px 10px;" +
                "text-align: center;" +
                "display: inline-block;" +
                "font-size: 20px;" +
                "margin: 10px;" +
                "text-decoration: none;" +
                "cursor: pointer;";
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
        helper.setFrom(mailProperties.getUsername());
        helper.setTo(toEmail);

        if (mailType == MailType.CLIENT_QUESTIONNAIRE) {
            helper.setSubject("Club Management Questionnaire");
            String bodyText = "<h2>Club Management Questionnaire Form</h2>";
            bodyText += "<h3>Please click in the given link to fill up the questionnaire</h3>";
            bodyText += "<hr>";
            bodyText += "<a href=\"http://localhost:4200/questionnaire/" +
                    serial + "\" style=\"" + buttonClass + "\">Fill Questionnaire</a>";
            helper.setText(bodyText, true);

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
