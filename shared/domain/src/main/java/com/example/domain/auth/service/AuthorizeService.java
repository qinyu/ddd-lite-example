package com.example.domain.auth.service;

import com.example.domain.auth.exception.AuthorizeException;
import com.example.domain.auth.model.Authorize;
import com.example.domain.auth.repository.AuthorizeRepository;
import com.example.domain.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthorizeService {

    private final AuthorizeRepository authorizeRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthorizeService(AuthorizeRepository authorizeRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.authorizeRepository = authorizeRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Authorize create(User user, String password) {
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw AuthorizeException.invalidCredential();
        }

        if (user.getStatus().equals(User.Status.FROZEN)) {
            throw AuthorizeException.userFrozen();
        }

        Authorize authorize = Authorize.builder()
                .userId(user.getId())
                .role(user.getRole())
                .build();
        return authorizeRepository.create(authorize);
    }

    public void delete(String id) {
        authorizeRepository.delete(id);
    }
}
