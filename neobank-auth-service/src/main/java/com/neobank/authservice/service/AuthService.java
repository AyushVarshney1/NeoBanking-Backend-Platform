package com.neobank.authservice.service;

import com.neobank.authservice.dto.AuthRequestDto;
import com.neobank.authservice.dto.AuthResponseDto;
import com.neobank.authservice.dto.UserResponseDto;
import com.neobank.authservice.model.User;
import com.neobank.authservice.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDto generateToken(AuthRequestDto authRequestDto) {

        String token = jwtUtil.generateToken(authRequestDto.getEmail(), authRequestDto.getPassword());
        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setToken(token);

        return authResponseDto;
    }

    public Optional<String> authenticate(AuthRequestDto authRequestDto) {
        Optional<String> token = userService.findUserByEmail(authRequestDto.getEmail())
                .filter(user -> passwordEncoder.matches(authRequestDto.getPassword(),user.getPassword()))
                .map(user -> jwtUtil.generateToken(user.getEmail(), user.getRole().toString()));

        return token;
    }

    public boolean validateToken(String token){
        try{
            jwtUtil.validateToken(token);
            return true;
        }catch(JwtException e){
            return false;
        }
    }

    public User extractUser(String token){

        String email = jwtUtil.extractEmail(token);
        Optional<User> optionalUser = userService.findUserByEmail(email);
        if(optionalUser.isEmpty()){
           throw new UsernameNotFoundException("User not found");
        }

        return optionalUser.get();
    }
}
