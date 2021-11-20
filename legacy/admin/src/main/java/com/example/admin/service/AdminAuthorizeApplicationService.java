package com.example.admin.service;

import com.example.admin.usecase.authorize.LoginCase;
import com.example.domain.auth.model.Authorize;
import com.example.domain.auth.service.AuthorizeService;
import com.example.domain.user.model.User;
import com.example.domain.user.service.UserService;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthorizeApplicationService {

    private final AuthorizeService authorizeService;
    private final UserService userService;

    public AdminAuthorizeApplicationService(AuthorizeService authorizeService, UserService userService) {
        this.authorizeService = authorizeService;
        this.userService = userService;
    }

    public LoginCase.Response login(LoginCase.Request request) {
        User user = userService.get(Example.of(User.builder()
                .name(request.getName())
                .role(User.Role.ADMIN)
                .build()));

        Authorize authorize = authorizeService.create(user, request.getPassword());
        return LoginCase.Response.from(authorize);
    }

    public void logout() {
        Authorize authorize = authorizeService.getCurrent();
        authorizeService.delete(authorize.getId());
    }
}
