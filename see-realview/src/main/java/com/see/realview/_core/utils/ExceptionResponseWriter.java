package com.see.realview._core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.see.realview._core.exception.CustomException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ExceptionResponseWriter {

    private final ObjectMapper objectMapper;


    public ExceptionResponseWriter(@Autowired ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void write(HttpServletResponse response, CustomException exception) throws IOException {
        response.resetBuffer();
        response.setStatus(exception.status().value());
        response.setContentType("application/json; charset=utf-8");

        String body = objectMapper.writeValueAsString(exception);
        response.getWriter().println(body);
    }
}
