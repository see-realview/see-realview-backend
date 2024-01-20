package com.see.realview._core.utils;

import com.see.realview.image.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataGenerator implements ApplicationRunner {

    private final ImageService imageService;


    public DataGenerator(@Autowired ImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.debug("어플리케이션 초기 데이터 생성 시작");
        imageService.rebase();
        imageService.rebaseWebDatabase();
        log.debug("어플리케이션 초기 데이터 생성 완료");
    }
}
