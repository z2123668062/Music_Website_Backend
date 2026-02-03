package org.example.music.service;

import org.example.music.dto.UserAuthDTO;

public interface AuthService {

    public UserAuthDTO validateJwtToken(String jwtToken);

}
