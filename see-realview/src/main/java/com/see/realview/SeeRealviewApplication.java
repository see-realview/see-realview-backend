package com.see.realview;

import com.see.realview._core.config.EnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@PropertySource(value = "classpath:properties/env.yaml",
                factory = EnvConfig.class)
@EnableScheduling
public class SeeRealviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeeRealviewApplication.class, args);
    }

}
