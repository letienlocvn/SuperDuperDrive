package com.udacity.jwdnd.course1.cloudstorage.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class FileUploadController {

    @PostMapping("/home")
    public String uploadFile() {

        return "home";
    }

}
