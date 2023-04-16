package com.example.SpringBootDemoApplication.exceptions;

public class NoteNotFoundException extends RuntimeException {
    public NoteNotFoundException(Long id) {
        super("Note not found with id: " + id);
    }
}
