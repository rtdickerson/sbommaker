package com.sbommaker.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @Value("${app.logo-url:/images/logo.svg}")
    private String logoUrl;

    @ModelAttribute("logoUrl")
    public String logoUrl() {
        return logoUrl;
    }
}
