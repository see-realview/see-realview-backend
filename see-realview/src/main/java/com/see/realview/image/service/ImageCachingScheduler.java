package com.see.realview.image.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ImageCachingScheduler {

    private final ImageService imageService;


    public ImageCachingScheduler(@Autowired ImageService imageService) {
        this.imageService = imageService;
    }

    @Scheduled(cron = "${api.image.cache-schedule}", zone = "Asia/Seoul")
    public void imageCachingTaskSchedule() {
        log.debug("[scheduler] 이미지 분석 캐시 데이터 rebase 시작");
        imageService.rebase();
        log.debug("[scheduler] 이미지 분석 캐시 데이터 rebase 완료");
    }

    @Scheduled(cron = "${api.image.well-known-cache-schedule}", zone = "Asia/Seoul")
    public void wellKnownImageCachingTaskSchedule() {
        log.debug("[scheduler] well-known urls 웹 데이터베이스 rebase 시작");
        imageService.rebaseWebDatabase();
        log.debug("[scheduler] well-known urls 웹 데이터베이스 rebase 완료");
    }
}