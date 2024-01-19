package com.see.realview;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
        "api.search.kakao.key=testKey",
        "api.search.naver.id=testId",
        "api.search.naver.secret:testSecret",
        "security.jwt.secret.access=a11111111111111111111a11111111111111111111a11111111111111111111a11111111111111111111",
        "security.jwt.secret.refresh=a11111111111111111111a11111111111111111111a11111111111111111111a11111111111111111111",
        "api.google.key=1111111111111111111111111111111111111111111111111",
        "api.google.gmail.sender=asdf@asdf.com",
        "api.google.gmail.password=qwer1234",
        "api.report.receiver-email=qwer1234",
        "api.image.cache-size=1000",
        "api.image.cache-schedule=0 0/10 * * * *",
        "api.image.expire=5",
        "api.image.well-known-cache-schedule=0 0 0 * * *",
        "redis.host=localhost"
})
@SpringBootTest(classes = SeeRealviewApplicationTests.class)
class SeeRealviewApplicationTests {

    @Test
    void contextLoads() {
    }

}
