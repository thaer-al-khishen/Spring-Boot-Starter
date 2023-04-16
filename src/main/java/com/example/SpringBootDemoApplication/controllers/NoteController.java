package com.example.SpringBootDemoApplication.controllers;

import com.example.SpringBootDemoApplication.exceptions.NoteNotFoundException;
import com.example.SpringBootDemoApplication.models.ErrorResponse;
import com.example.SpringBootDemoApplication.models.Note;
import com.example.SpringBootDemoApplication.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes() {
        return ResponseEntity.ok(noteService.findAllNotes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        Note note = noteService.findNoteById(id).orElseThrow(() -> new NoteNotFoundException(id));
        return ResponseEntity.ok(note);
    }

    @PostMapping
    public ResponseEntity<?> createNote(@Valid @RequestBody Note note) {
        return ResponseEntity.ok(noteService.saveOrUpdateNote(note));
    }

//    @PostMapping
//    public ResponseEntity<?> createNote(@Valid @RequestBody Note note, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            Map<String, String> errors = new HashMap<>();
//            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
//            return ResponseEntity.badRequest().body(errors);
//        }
//        return ResponseEntity.ok(noteService.saveOrUpdateNote(note));
//    }

//    @PostMapping
//    public ResponseEntity<?> createNote(@Valid @RequestBody Note note, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            Map<String, String> errors = new HashMap<>();
//            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
//            ErrorResponse errorResponse = new ErrorResponse();
//            errorResponse.setTimestamp(LocalDateTime.now());
//            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
//            errorResponse.setError("Not Found");
//            errorResponse.setMessage("Validation failed");
//            errorResponse.setFieldErrors(errors);
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//        return ResponseEntity.ok(noteService.saveOrUpdateNote(note));
//    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable Long id, @Valid @RequestBody Note updatedNote, BindingResult result) {
        if (result.hasErrors()) {
            // Handle validation errors here, e.g., return a custom error response
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        Note existingNote = noteService.findNoteById(id).orElseThrow(() -> new NoteNotFoundException(id));
        existingNote.setTitle(updatedNote.getTitle());
        existingNote.setContent(updatedNote.getContent());
        Note savedNote = noteService.saveOrUpdateNote(existingNote);
        return ResponseEntity.ok(savedNote);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        noteService.deleteNoteById(id);
        return ResponseEntity.noContent().build();
    }

}
