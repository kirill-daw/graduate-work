package com.skypro.avito.service.impl;

import com.skypro.avito.dto.UpdateUser;
import com.skypro.avito.dto.User;
import com.skypro.avito.exception.InvalidPasswordException;
import com.skypro.avito.exception.UserNotFoundException;
import com.skypro.avito.mapper.UserMapper;
import com.skypro.avito.repository.UserRepository;
import com.skypro.avito.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toUser)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    @Override
    public User updateUser(String username, UpdateUser updateUser) {
        var userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        userEntity.setFirstName(updateUser.getFirstName());
        userEntity.setLastName(updateUser.getLastName());
        userEntity.setPhone(updateUser.getPhone());
        userRepository.save(userEntity);
        return userMapper.toUser(userEntity);
    }

    @Override
    public void changePassword(String username, String currentPassword, String newPassword) {
        var userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        if (!passwordEncoder.matches(currentPassword, userEntity.getPassword())) {
            throw new InvalidPasswordException();
        }
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
    }

    @Override
    public void updateAvatar(String username, String filename) {
        var userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        userEntity.setImage(filename);
        userRepository.save(userEntity);
    }
}
