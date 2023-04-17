package com.example.SpringBootDemoApplication.services;

import com.example.SpringBootDemoApplication.models.Story;
import com.example.SpringBootDemoApplication.models.auth.AppUser;
import com.example.SpringBootDemoApplication.repositories.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StoryService {
    @Autowired
    private StoryRepository storyRepository;

    public List<Story> findAllStories(Long id) {
        return storyRepository.findByAppUserId(id);
    }

    public List<Story> getAllStoriesAdmin() {
        return storyRepository.findAll();
    }

    public Optional<Story> findStoryById(Long storyId) {
        return storyRepository.findById(storyId);
    }

    public Story saveOrUpdateStory(Story story, AppUser appUser) {
        story.setUser(appUser);
        return storyRepository.save(story);
    }

    public Story updateStory(Story story) {
        return storyRepository.save(story);
    }

    public void deleteStoryById(Long id) {
        storyRepository.deleteById(id);
    }
}
