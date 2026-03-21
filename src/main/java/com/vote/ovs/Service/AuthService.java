package com.vote.ovs.Service;

import com.vote.ovs.Entity.User;
import com.vote.ovs.Repository.UserRepository;
import com.vote.ovs.Security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Secret key to protect admin registration (change this in production)
    private static final String ADMIN_SECRET = "admin-secret-key-2026";

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String register(String username, String password) {

        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role("USER")
                .hasVoted(false)
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }

    public String registerAdmin(String username, String password, String adminSecret) {

        if (!ADMIN_SECRET.equals(adminSecret)) {
            throw new RuntimeException("Invalid admin secret");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role("ADMIN")
                .hasVoted(false)
                .build();

        userRepository.save(user);

        return "Admin registered successfully";
    }

    public String login(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(username, user.getRole());
    }
}