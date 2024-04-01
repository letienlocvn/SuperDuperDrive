package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.entity.File;
import com.udacity.jwdnd.course1.cloudstorage.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.services.FileUploadService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private NoteService noteService;
    private FileUploadService fileUploadService;

    public HomeController(NoteService noteService, FileUploadService fileUploadService) {
        this.noteService = noteService;
        this.fileUploadService = fileUploadService;
    }

    @GetMapping("/home")
    public String getHomePage(Authentication authentication, Model model) {
        // List notes
        List<Note> notes = noteService.getAllNotes(authentication.getName());
        model.addAttribute("notes", notes);

        // List file
        List<File> files = fileUploadService.getAllFiles(authentication.getName());
        model.addAttribute("files", files);
        return "home";
    }
}
