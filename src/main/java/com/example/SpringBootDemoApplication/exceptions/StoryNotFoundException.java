package com.example.SpringBootDemoApplication.exceptions;

public class StoryNotFoundException extends RuntimeException {
    public StoryNotFoundException() {
        super("Story not found");
    }
}
