package com.skypro.avito.service;

import com.skypro.avito.dto.RegisterReq;

public interface AuthService {
    boolean login(String userName, String password);

    boolean register(RegisterReq registerReq);
}
