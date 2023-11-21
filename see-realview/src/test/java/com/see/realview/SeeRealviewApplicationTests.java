package com.see.realview;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
        "api.search.kakao.key=testKey",
        "api.search.naver.id=testId",
        "api.search.naver.secret:testSecret",
        "security.jwt.secret.access=a11111111111111111111a11111111111111111111a11111111111111111111a11111111111111111111",
        "security.jwt.secret.refresh=a11111111111111111111a11111111111111111111a11111111111111111111a11111111111111111111"
})
@SpringBootTest
class SeeRealviewApplicationTests {

    @Test
    void contextLoads() {
    }

}
