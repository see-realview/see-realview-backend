package com.see.realview.application.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {

    @GetMapping("/")
    public String home() {
        return "main";
    }

    @GetMapping("/bug-report")
    public String bugReport() {
        return "bug-report";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
