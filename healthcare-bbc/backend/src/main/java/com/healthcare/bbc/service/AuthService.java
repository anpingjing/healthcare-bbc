package com.healthcare.bbc.service;

import com.healthcare.bbc.dto.LoginDTO;
import com.healthcare.bbc.vo.LoginVO;

public interface AuthService {
    LoginVO login(LoginDTO loginDTO);
    void logout(String token);
}
