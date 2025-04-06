package com.github.cyberxandrew.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping(path = "/welcome")
public class WelcomeController {

    @GetMapping(path = "/welcome")
    @ResponseStatus(HttpStatus.OK)
    public String welcome() {
        return "Hello!";
    }

    @GetMapping(path = "/welcome/protected")
    @ResponseStatus(HttpStatus.OK)
    public String welcomeProtected() {
        return "Hello, protected world!";
    }

    @GetMapping(path = "/welcome/admin")
    @ResponseStatus(HttpStatus.OK)
    public String admin() {
        return "This is admin page!";
    }

}
