package com.sbommaker.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @Value("${app.name:SBOM Maker}")
    private String appName;

    @Value("${app.logo-url:/images/logo.svg}")
    private String logoUrl;

    @ModelAttribute("appName")
    public String appName() {
        return appName;
    }

    @ModelAttribute("logoUrl")
    public String logoUrl() {
        return logoUrl;
    }
}
