package com.vote.ovs.Controller;

import com.vote.ovs.Dto.AdminRegisterRequest;
import com.vote.ovs.Dto.AuthRequest;
import com.vote.ovs.Dto.AuthResponse;
import com.vote.ovs.Service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody AuthRequest request) {
        return Map.of("message", authService.register(request.getUsername(), request.getPassword()));
    }

    @PostMapping("/register-admin")
    public Map<String, String> registerAdmin(@RequestBody AdminRegisterRequest request) {
        return Map.of("message", authService.registerAdmin(
                request.getUsername(),
                request.getPassword(),
                request.getAdminSecret()
        ));
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        return new AuthResponse(token);
    }
}