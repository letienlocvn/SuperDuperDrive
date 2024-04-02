package com.udacity.jwdnd.course1.cloudstorage.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class ResultController {

    @GetMapping("/result")
    public String showResultPage(@ModelAttribute("successMessage") String successMessage,
                                 @ModelAttribute("errorMessage") String errorMessage) {
        return "result";
    }
}
