package com.see.realview.code.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.server.ServerException;
import com.see.realview.code.entity.EmailCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class EmailCodeRedisRepositoryImpl implements EmailCodeRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private final ValueOperations<String, String> valueOperations;

    private final ObjectMapper objectMapper;

    private final static String EMAIL_PREFIX = "email_";

    private final static Duration CODE_EXP = Duration.ofMinutes(10);


    public EmailCodeRedisRepositoryImpl(@Autowired RedisTemplate<String, String> redisTemplate,
                                        @Autowired ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(String email, String code) {
        EmailCode emailCode = new EmailCode(code, false);

        String key = getKey(email);
        String value = getValue(emailCode);

        valueOperations.set(key, value, CODE_EXP);
    }

    @Override
    public Optional<EmailCode> findCodeByEmail(String email) {
        String key = getKey(email);
        String value = valueOperations.get(key);

        System.out.println(value);

        if (value == null) {
            return Optional.empty();
        }

        EmailCode code = getEmailCode(value);
        return Optional.of(code);
    }

    @Override
    public void authenticated(String email) {
        String key = getKey(email);
        String value = valueOperations.get(key);

        EmailCode emailCode = getEmailCode(value)
                .updateEmailAuthenticated();

        value = getValue(emailCode);
        valueOperations.set(key, value);
    }

    @Override
    public void delete(String email) {
        String key = getKey(email);
        redisTemplate.delete(key);
    }

    private String getKey(String email) {
        return EMAIL_PREFIX + email;
    }

    private String getValue(EmailCode data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new ServerException(ExceptionStatus.DATA_CONVERSION_ERROR);
        }
    }

    private EmailCode getEmailCode(String value) {
        try {
            return objectMapper.readValue(value, EmailCode.class);
        } catch (JsonProcessingException e) {
            throw new ServerException(ExceptionStatus.DATA_CONVERSION_ERROR);
        }
    }
}
