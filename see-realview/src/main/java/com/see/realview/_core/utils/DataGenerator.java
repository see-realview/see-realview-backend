package com.see.realview._core.utils;

import com.see.realview.image.repository.impl.ParsedImageRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataGenerator implements ApplicationRunner {

    private final ParsedImageRepositoryImpl parsedImageRepository;


    public DataGenerator(@Autowired ParsedImageRepositoryImpl parsedImageRepository) {
        this.parsedImageRepository = parsedImageRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        insertParseImageData();
    }

    private void insertParseImageData() {
    }
}
