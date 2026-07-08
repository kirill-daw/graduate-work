package com.skypro.avito.service.impl;

import com.skypro.avito.dto.RegisterReq;
import com.skypro.avito.entity.UserEntity;
import com.skypro.avito.mapper.UserMapper;
import com.skypro.avito.repository.UserRepository;
import com.skypro.avito.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.encoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public boolean login(String userName, String password) {
        return userRepository.findByUsername(userName)
                .map(user -> encoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    @Override
    public boolean register(RegisterReq registerReq) {
        if (userRepository.findByUsername(registerReq.getUsername()).isPresent()) {
            return false;
        }
        UserEntity userEntity = userMapper.toEntity(registerReq);
        userEntity.setPassword(encoder.encode(registerReq.getPassword()));
        userRepository.save(userEntity);
        return true;
    }
}
