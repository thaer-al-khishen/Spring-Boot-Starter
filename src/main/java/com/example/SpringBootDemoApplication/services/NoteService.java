package com.example.SpringBootDemoApplication.services;

import com.example.SpringBootDemoApplication.models.Note;
import com.example.SpringBootDemoApplication.repositories.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    public List<Note> findAllNotes() {
        return noteRepository.findAll();
    }

    public Optional<Note> findNoteById(Long id) {
        return noteRepository.findById(id);
    }

    public Note saveOrUpdateNote(Note note) {
        return noteRepository.save(note);
    }

    public void deleteNoteById(Long id) {
        noteRepository.deleteById(id);
    }
}
