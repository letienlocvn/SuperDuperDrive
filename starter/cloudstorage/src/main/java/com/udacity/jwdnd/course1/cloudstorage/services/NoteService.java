package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NoteService {

    private NoteMapper noteMapper;
    private UserMapper userMapper;

    public NoteService(NoteMapper noteMapper, UserMapper userMapper) {
        this.noteMapper = noteMapper;
        this.userMapper = userMapper;
    }

    public int insertNote(String username, Note note) {
        Integer userId;
        if (userMapper.getUser(username) != null) {
            userId = userMapper.getUser(username).getUserId();
            note.setUserId(userId);
            return noteMapper.insertNote(note);
        }
        return -1;
    }

    public List<Note> getAllNotes(String username) throws Exception {
        Integer userId;
        if (userMapper.getUser(username) != null) {
            userId = userMapper.getUser(username).getUserId();
            return noteMapper.getNotesByUserId(userId);
        } else {
            throw new Exception("User not exited, please try again");
        }
    }

    public int updateNote(String username, Note note) {
        Integer userId;
        if (userMapper.getUser(username) != null) {
            userId = userMapper.getUser(username).getUserId();
            note.setUserId(userId);
            return noteMapper.updateNote(note);
        }
        return -1;
    }

    public int deleteNote(Integer noteId) {
        return noteMapper.deleteNote(noteId);
    }
}
