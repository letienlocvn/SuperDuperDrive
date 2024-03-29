package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.entity.User;
import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SignupController {

    private UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String getSignUpPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute User user, Model model) {
        String errorMessage = null;

        if (!userService.isUsernameAvailable(user.getUsername())) {
            errorMessage = "Username already exists";
        }

        if (errorMessage == null) {
            int row = userService.createUser(user);
            if (row < 0) {
                errorMessage = "There was an error signing you up. Please try again.";
            }
        }

        if (errorMessage == null) {
            model.addAttribute("signupSuccess", true);
        } else {
            model.addAttribute("signupError", errorMessage);
        }

        return "signup";
    }
}
