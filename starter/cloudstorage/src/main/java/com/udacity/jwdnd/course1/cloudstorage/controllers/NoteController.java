package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Objects;

@Controller
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping("/note")
    public String uploadNoteOrEditNote(Note note,
                                       Authentication authentication,
                                       Model model) {
        String errorMessage;
        String username = authentication.getName();
        int rowEffected = 0;
        if (note.getNoteId() == null) {
            System.out.println("Call insert Note");
            rowEffected = noteService.insertNote(username, note);
            errorMessage = "Add new note successfully";
        } else {
            System.out.println("Call update Note");
            rowEffected = noteService.updateNote(username, note);
            errorMessage = "Edit note successfully";
        }
        if (rowEffected < 0) {
            errorMessage = "Some thing wrong, please check it again";
        }

        model.addAttribute("errorMessage", Objects.requireNonNullElse(errorMessage, false));
        return "redirect:/home";
    }


    public String deleteNote() {
        return "";
    }
}
