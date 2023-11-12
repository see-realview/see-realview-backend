package com.see.realview;

import com.see.realview.core.config.EnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = "classpath:properties/env.yaml",
                factory = EnvConfig.class)
public class SeeRealviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeeRealviewApplication.class, args);
    }

}
