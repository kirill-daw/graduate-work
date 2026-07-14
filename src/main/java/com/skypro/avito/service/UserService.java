package com.skypro.avito.service;

import com.skypro.avito.dto.UpdateUser;
import com.skypro.avito.dto.User;

public interface UserService {

    User getUserByUsername(String username);

    User updateUser(String username, UpdateUser updateUser);

    void changePassword(String username, String currentPassword, String newPassword);

    void updateAvatar(String username, String filename);
}
