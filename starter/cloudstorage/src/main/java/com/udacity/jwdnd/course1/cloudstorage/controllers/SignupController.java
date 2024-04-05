package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.entity.User;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        String message = null;

        if (!userService.isUsernameAvailable(user.getUsername())) {
            message = "Username already exists. Please use another username.";
        }

        if (message == null) {
            int row = userService.createUser(user);
            if (row < 0) {
                message = "Something was wrong. Please try again.";
            }
        }
        if (message != null) {
            model.addAttribute("signupError", message);
            return "signup";
        } else {
            boolean signupSuccess = true;
            model.addAttribute("signupSuccess", signupSuccess);
            return "redirect:/login";
        }
    }
}
