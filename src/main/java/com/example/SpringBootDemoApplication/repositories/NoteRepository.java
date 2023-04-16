package com.example.SpringBootDemoApplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.SpringBootDemoApplication.models.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {}
