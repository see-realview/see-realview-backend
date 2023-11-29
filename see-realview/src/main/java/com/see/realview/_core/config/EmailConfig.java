package com.see.realview._core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${api.google.gmail.sender}")
    private String GMAIL_SENDER;

    @Value("${api.google.gmail.password}")
    private String GMAIL_PASSWORD;

    @Bean
    public JavaMailSenderImpl javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setUsername(GMAIL_SENDER);
        javaMailSender.setPassword(GMAIL_PASSWORD);
        javaMailSender.setPort(587);
        javaMailSender.setJavaMailProperties(getEmailProperties());

        return javaMailSender;
    }

    @Bean
    public Properties getEmailProperties() {
        Properties properties = new Properties();

        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.debug", "true");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.timeout", "10000");
        properties.setProperty("mail.smtp.starttls.enable", "true");

        return properties;
    }
}
