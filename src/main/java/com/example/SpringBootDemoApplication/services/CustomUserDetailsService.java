package com.example.SpringBootDemoApplication.services;

import com.example.SpringBootDemoApplication.jwt.UserDetailsImpl;
import com.example.SpringBootDemoApplication.models.auth.Role;
import com.example.SpringBootDemoApplication.models.auth.User;
import com.example.SpringBootDemoApplication.repositories.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        // Return an instance of UserDetailsImpl instead of org.springframework.security.core.userdetails.User
        return new UserDetailsImpl(user.getId(), user.getUsername(), user.getPassword(), authorities);
    }
}
