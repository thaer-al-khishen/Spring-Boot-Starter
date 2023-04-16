package com.example.SpringBootDemoApplication.repositories;

import com.example.SpringBootDemoApplication.models.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findByUserId(Long userId);
    Optional<Story> getByUserId(Long userId);
}
