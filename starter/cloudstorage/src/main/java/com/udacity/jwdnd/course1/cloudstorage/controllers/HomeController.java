package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private NoteService noteService;

    public HomeController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/home")
    public String getHomePage(Authentication authentication, Model model) {
        List<Note> notes = noteService.getAllNotes(authentication.getName());
        System.out.println("This is list Note " + notes.toString());
        model.addAttribute("notes", notes);
        return "home";
    }
}
