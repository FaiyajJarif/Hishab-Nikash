package com.Eqinox.store.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "homePage";
    }

    @GetMapping("/login")
    public String login() {
        return "loginPage";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signupPage";
    }

    @GetMapping("/onboarding")
    public String onboarding() {
        return "onboarding";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
