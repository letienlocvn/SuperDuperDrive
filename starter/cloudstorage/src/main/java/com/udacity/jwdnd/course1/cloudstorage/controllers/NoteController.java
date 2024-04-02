package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping("/note")
    public String uploadNoteOrEditNote(Note note,
                                       Authentication authentication,
                                       RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        int rowEffected = 0;
        String message = null;
        if (note.getNoteId() == null) {
            rowEffected = noteService.insertNote(username, note);
            if (rowEffected > 0) {
                message = "New note added successfully.";
            } else {
                message = "Failed to add new note.";
            }
        } else {
            rowEffected = noteService.updateNote(username, note);
            if (rowEffected > 0) {
                message = "Note edited successfully.";
            } else {
                message = "Failed to edit note.";
            }
        }

        redirectAttributes.addFlashAttribute((rowEffected > 0) ? "successMessage" : "errorMessage", message);

        return "redirect:/result";
    }


    @GetMapping("/notes/delete/{noteId}")
    public String deleteNote(@PathVariable(name = "noteId") Integer noteId,
                             RedirectAttributes redirectAttributes) {
        int rowEffected = noteService.deleteNote(noteId);
        String message;
        if (rowEffected > 0) {
            message = "Note deleted successfully.";
        } else {
            message = "An error occurred while deleting the note. Please try again.";
        }
        redirectAttributes.addFlashAttribute((rowEffected > 0) ? "successMessage" : "errorMessage", message);
        return "redirect:/result";
    }
}
