package com.see.realview._core.utils;

import com.see.realview.image.service.ParsedImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataGenerator implements ApplicationRunner {

    private final ParsedImageService parsedImageService;


    public DataGenerator(@Autowired ParsedImageService parsedImageService) {
        this.parsedImageService = parsedImageService;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.debug("어플리케이션 초기 데이터 생성 시작");
        parsedImageService.rebase();
        parsedImageService.rebaseWebDatabase();
        log.debug("어플리케이션 초기 데이터 생성 완료");
    }
}
