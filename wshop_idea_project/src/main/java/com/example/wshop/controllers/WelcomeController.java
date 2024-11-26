package com.example.wshop.controllers;

import com.example.wshop.model.User;
import com.example.wshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    private final UserService userService;

    @Autowired
    public WelcomeController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/")
    public String welcomeString(){
        User user = userService.getCurrentUser();
        return user.getUsername()+" welcome to WShop!";
    }

}
