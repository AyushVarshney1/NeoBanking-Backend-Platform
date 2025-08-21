package com.neobank.authservice.service;

import com.neobank.authservice.dto.AuthRequestDto;
import com.neobank.authservice.exception.UserAlreadyExistsException;
import com.neobank.authservice.model.User;
import com.neobank.authservice.model.enums.Role;
import com.neobank.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void createUser(AuthRequestDto authRequestDto){

        User user = new User();
        user.setEmail(authRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(authRequestDto.getPassword()));
        user.setRole(Role.valueOf(authRequestDto.getRole().toUpperCase()));

        if(userRepository.findByEmail(authRequestDto.getEmail()).isPresent()){
            throw new UserAlreadyExistsException("User already exists");
        }

        userRepository.save(user);
    }

    public Optional<User> findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
