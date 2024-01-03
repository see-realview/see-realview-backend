package com.see.realview.report.service.impl;

import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.server.ServerException;
import com.see.realview.report.dto.request.ReportRequest;
import com.see.realview.report.service.ReportService;
import com.see.realview.report.service.constant.ReportType;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class ReportServiceImpl implements ReportService {

    private final JavaMailSender javaMailSender;

    @Value("${api.google.gmail.sender}")
    private String SENDER;

    @Value("${api.report.receiver-email}")
    private String RECEIVER;


    public ReportServiceImpl(@Autowired JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void send(ReportType type, ReportRequest request) {
        try {
            String reportTitle = type.toString() + " " + request.title();
            MimeMessage message = javaMailSender.createMimeMessage();
            message.setFrom(new InternetAddress(SENDER, "see-realview"));
            message.addRecipients(Message.RecipientType.TO, RECEIVER);
            message.setSubject(reportTitle);
            message.setText(request.content(), "utf-8", "text");
            javaMailSender.send(message);
        }
        catch (MessagingException | UnsupportedEncodingException exception) {
            throw new ServerException(ExceptionStatus.EMAIL_CONTENT_CREATE_ERROR);
        }
    }
}
