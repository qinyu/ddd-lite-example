package com.example.business.service;

import com.example.domain.auth.model.Authorize;
import com.example.domain.auth.service.AuthorizeService;
import com.example.domain.user.model.User;
import com.example.domain.user.service.UserService;
import com.example.business.usecase.authorize.GetUserProfileCase;
import com.example.business.usecase.authorize.LoginCase;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Service
public class AuthorizeApplicationService {

    private final AuthorizeService authorizeService;
    private final UserService userService;

    public AuthorizeApplicationService(AuthorizeService authorizeService, UserService userService) {
        this.authorizeService = authorizeService;
        this.userService = userService;
    }

    public LoginCase.Response login(LoginCase.Request request) {
        User user = userService.get(Example.of(User.builder()
                .email(request.getEmail())
                .role(User.Role.USER)
                .build()));

        Authorize authorize = authorizeService.create(user, request.getPassword());
        return LoginCase.Response.from(authorize);
    }

    public void logout() {
        Authorize authorize = authorizeService.getCurrent();
        authorizeService.delete(authorize.getId());
    }

    public GetUserProfileCase.Response getProfile() {
        Authorize authorize = authorizeService.getCurrent();
        return GetUserProfileCase.Response.from(authorize);
    }
}
