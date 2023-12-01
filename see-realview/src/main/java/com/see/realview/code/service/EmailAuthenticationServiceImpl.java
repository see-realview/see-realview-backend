package com.see.realview.code.service;

import com.see.realview._core.exception.ExceptionStatus;
import com.see.realview._core.exception.client.BadRequestException;
import com.see.realview._core.exception.client.NotFoundException;
import com.see.realview._core.exception.server.ServerException;
import com.see.realview.code.dto.VerifyEmailRequest;
import com.see.realview.code.entity.EmailCode;
import com.see.realview.code.repository.EmailCodeRedisRepositoryImpl;
import com.see.realview.user.service.UserServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.IntStream;

@Service
public class EmailAuthenticationServiceImpl implements EmailAuthenticationService {

    private final EmailCodeRedisRepositoryImpl emailCodeRedisRepository;

    private final UserServiceImpl userService;

    private final JavaMailSenderImpl javaMailSender;

    private final static int AUTHENTICATION_CODE_LENGTH = 6;

    private final static String EMAIL_CONTENT = """
            <body>
                    <div style="display: flex; min-width: 320px; max-width: 500px; padding: 30px 30px; flex-direction: column; justify-content: center; align-items: flex-start; gap: 20px;">
                        <div style="color: #000; font-size: 20px; font-style: normal; font-weight: 400; line-height: normal; align-self: stretch;">
                            see-realview
                        </div>
                        <div style="color: #000; font-size: 22px; font-style: normal; font-weight: 700; line-height: normal; align-self: stretch;">
                            이메일 인증
                        </div>
                        <div style="color: #000; font-size: 16px; font-style: normal; font-weight: 400; line-height: normal; align-self: stretch;">
                            안녕하세요. see-realview입니다.<br />
                            요청하신 인증코드는 아래와 같습니다. 인증코드는 30분 동안 유효합니다.
                        </div>
                        <div style="display: flex; height: 100px; justify-content: center; align-items: center; gap: 10px; align-self: stretch; background: #DDD;
                        color: #000; font-size: 22px; font-style: normal; font-weight: 400; line-height: normal; letter-spacing: 2px;">
                            %s
                        </div>
                        <div style="color: #000; font-size: 14px; font-style: normal; font-weight: 400; line-height: normal; align-self: stretch;">
                            발신 전용 이메일이므로 이 주소로 회신하지 마세요.
                        </div>
                    </div>
                </body>
            """;

    @Value("${api.google.gmail.sender}")
    private String SENDER;


    public EmailAuthenticationServiceImpl(@Autowired EmailCodeRedisRepositoryImpl emailCodeRedisRepository,
                                          @Autowired UserServiceImpl userService,
                                          @Autowired JavaMailSenderImpl javaMailSender) {
        this.emailCodeRedisRepository = emailCodeRedisRepository;
        this.userService = userService;
        this.javaMailSender = javaMailSender;
    }

    @Override
    @Transactional
    public void send(String email) {
        userService.findByEmail(email)
                .ifPresent((user) -> {
                    throw new BadRequestException(ExceptionStatus.EMAIL_ALREADT_REGISTERED);
                });

        String code = createAuthenticationCode();
        MimeMessage message = createMessage(email, code);
        emailCodeRedisRepository.save(email, code);
        javaMailSender.send(message);
    }

    @Override
    public void check(VerifyEmailRequest request) {
        EmailCode emailCode = emailCodeRedisRepository.findCodeByEmail(request.email())
                .orElseThrow(() -> new NotFoundException(ExceptionStatus.EMAIL_AUTHENTICATION_CODE_NOT_FOUND));

        if (!request.code().equals(emailCode.code())) {
            throw new BadRequestException(ExceptionStatus.EMAIL_AUTHENTICATION_CODE_NOT_MATCHED);
        }

        emailCodeRedisRepository.authenticated(request.email());
    }

    private String createAuthenticationCode() {
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            IntStream.range(0, AUTHENTICATION_CODE_LENGTH)
                    .forEach(i -> builder.append(random.nextInt(10)));

            return builder.toString();
        }
        catch (NoSuchAlgorithmException exception) {
            throw new ServerException(ExceptionStatus.EMAIL_AUTHENTICATION_CODE_GENERATE_ERROR);
        }
    }

    private MimeMessage createMessage(String receiver, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            String content = String.format(EMAIL_CONTENT, code);

            message.setFrom(new InternetAddress(SENDER, "see-realview"));
            message.addRecipients(MimeMessage.RecipientType.TO, receiver);
            message.setSubject("see-realview 이메일 인증 코드입니다.");
            message.setText(content, "utf-8", "html");

            return message;
        }
        catch (MessagingException | UnsupportedEncodingException exception) {
            throw new ServerException(ExceptionStatus.EMAIL_CONTENT_CREATE_ERROR);
        }
    }
}
