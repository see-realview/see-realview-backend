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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void send(ReportType type, ReportRequest request) throws MessagingException, UnsupportedEncodingException {
        String reportTitle = type.toString() + " " + request.title();
        MimeMessage message = javaMailSender.createMimeMessage();
        String content = replaceExpletives(request.content());

        message.setFrom(new InternetAddress(SENDER, "see-realview"));
        message.addRecipients(Message.RecipientType.TO, RECEIVER);
        message.setSubject(reportTitle);
        message.setText(content, "utf-8", "text");
        javaMailSender.send(message);
    }

    @Override
    public String replaceExpletives(String content) {
        // TODO: 비속어 DB 연결하기
        String expletives = "(욕1|욕2|욕3)";

        Pattern pattern = Pattern.compile(expletives, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);

        StringBuilder replacedContent = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(replacedContent, "*");
        }
        matcher.appendTail(replacedContent);

        return replacedContent.toString();
    }
}
