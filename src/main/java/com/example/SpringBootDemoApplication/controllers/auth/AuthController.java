package com.example.SpringBootDemoApplication.controllers.auth;

import com.example.SpringBootDemoApplication.jwt.JwtUtil;
import com.example.SpringBootDemoApplication.jwt.UserDetailsImpl;
import com.example.SpringBootDemoApplication.models.auth.*;
import com.example.SpringBootDemoApplication.repositories.auth.RoleRepository;
import com.example.SpringBootDemoApplication.repositories.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        User user = createUser(registerRequest);
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticate(loginRequest);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtil.generateToken(userDetails.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return buildJwtResponse(userDetails, jwt, refreshToken);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        UserDetailsImpl userDetails = loadUserDetailsFromRefreshToken(refreshToken);

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token.");
        }

        if (jwtUtil.validateRefreshToken(refreshToken, userDetails)) {
            String accessToken = jwtUtil.generateToken(userDetails.getUsername());
            return buildJwtResponse(userDetails, accessToken, refreshToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token.");
        }
    }

    private User createUser(RegisterRequest registerRequest) {
        User user = new User(registerRequest.getUsername(),
                encoder.encode(registerRequest.getPassword()));
        Set<Role> roles = getRolesForUser(registerRequest.getRoles());
        user.setRoles(roles);
        return user;
    }

    private Set<Role> getRolesForUser(Set<String> strRoles) {
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            roles.add(getRoleByName("ROLE_USER"));
        } else {
            for (String role : strRoles) {
                roles.add("admin".equals(role) ? getRoleByName("ROLE_ADMIN") : getRoleByName("ROLE_USER"));
            }
        }

        return roles;
    }

    private Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    }

    private Authentication authenticate(LoginRequest loginRequest) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
    }

    private UserDetailsImpl loadUserDetailsFromRefreshToken(String refreshToken) {
        try {
            String username = jwtUtil.extractUsername(refreshToken, jwtUtil.getRefreshTokenSecret());
            return (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
        } catch (Exception e) {
            return null;
        }
    }

    private ResponseEntity<?> buildJwtResponse(UserDetailsImpl userDetails, String jwt, String refreshToken) {
        JwtResponse jwtResponse = new JwtResponse(jwt, refreshToken, userDetails.getId(), userDetails.getUsername(),
                userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return ResponseEntity.ok(jwtResponse);
    }

}
