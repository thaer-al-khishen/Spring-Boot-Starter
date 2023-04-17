package com.example.SpringBootDemoApplication.controllers;

import com.example.SpringBootDemoApplication.jwt.JwtUtil;
import com.example.SpringBootDemoApplication.jwt.UserDetailsImpl;
import com.example.SpringBootDemoApplication.models.Story;
import com.example.SpringBootDemoApplication.models.auth.AppUser;
import com.example.SpringBootDemoApplication.repositories.auth.UserRepository;
import com.example.SpringBootDemoApplication.services.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/stories")
public class StoryController {

    @Autowired
    private StoryService storyService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Story>> getStoriesByUserId(HttpServletRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        AppUser appUser = userRepository.findById(userDetails.getId()).orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            List<Story> stories = storyService.getAllStoriesAdmin();
            return ResponseEntity.ok(stories);
        } else {
            Long userId = appUser.getId();
            List<Story> stories = storyService.findAllStories(userId);
            return ResponseEntity.ok(stories);
        }

    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Story> createStory(@Valid @RequestBody Story story, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        AppUser appUser = userRepository.findById(userDetails.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        Story createdStory = storyService.saveOrUpdateStory(story, appUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStory);
    }

    @GetMapping("/{storyId}")
    public ResponseEntity<Story> getStoryById(@PathVariable("storyId") Long storyId) {
        Story story = storyService.findStoryById(storyId).orElseThrow(() -> new RuntimeException("Can't find story"));
        return ResponseEntity.ok(story);
    }

    @PutMapping("/{storyId}")
    public ResponseEntity<Story> updateStory(@PathVariable("storyId") Long storyId, @RequestBody Story storyDetails, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Story currentStory = storyService.findStoryById(storyId).orElseThrow(() -> new RuntimeException());
        currentStory.setName(storyDetails.getName());
        currentStory.setMessage(storyDetails.getMessage());
        Story updatedStory = storyService.updateStory(currentStory);
        return ResponseEntity.ok(updatedStory);
    }

    @DeleteMapping("/{storyId}")
    public ResponseEntity<String> deleteStory(@PathVariable("storyId") Long storyId) {
        storyService.deleteStoryById(storyId);
        return ResponseEntity.ok("Story with ID " + storyId + " was deleted.");
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }

    private AppUser getUser(HttpServletRequest request) {
        String jwt = parseJwt(request);
        String username = jwtUtil.extractUsername(jwt);
        return userRepository.findByUsername(username).orElse(null);
    }

}
