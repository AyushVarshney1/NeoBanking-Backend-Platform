package com.neobank.authservice.controller;

import com.neobank.authservice.dto.AuthRequestDto;
import com.neobank.authservice.dto.AuthResponseDto;
import com.neobank.authservice.dto.UserResponseDto;
import com.neobank.authservice.dto.validator.SignUpValidationGroup;
import com.neobank.authservice.exception.UserNotFoundException;
import com.neobank.authservice.model.User;
import com.neobank.authservice.service.AuthService;
import com.neobank.authservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signup(@Validated({Default.class, SignUpValidationGroup.class}) @Valid @RequestBody AuthRequestDto authRequestDto) {
        userService.createUser(authRequestDto);
        AuthResponseDto authResponseDto = authService.generateToken(authRequestDto);

        return ResponseEntity.ok(authResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Validated({Default.class}) @Valid @RequestBody AuthRequestDto authRequestDto) {
        User user = userService.findUserByEmail(authRequestDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User with email: " + authRequestDto.getEmail() + " not found"));

        Optional<String> optionalToken = authService.authenticate(authRequestDto);

        if (!optionalToken.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = optionalToken.get();

        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setToken(token);

        return ResponseEntity.ok(authResponseDto);
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validate(@RequestHeader("Authorization") String token){

        if(token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return authService.validateToken(token.substring(7)) ? ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/extract-user")
    public ResponseEntity<UserResponseDto> extractUserFromToken(@RequestHeader("Authorization") String token){

        if(token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = authService.extractUser(token.substring(7));
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setRole(user.getRole());

        return ResponseEntity.ok(userResponseDto);
    }
}
