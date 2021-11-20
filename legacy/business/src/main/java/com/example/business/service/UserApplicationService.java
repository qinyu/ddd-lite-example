package com.example.business.service;

import com.example.domain.user.model.Operator;
import com.example.domain.user.model.User;
import com.example.domain.user.service.UserService;
import com.example.business.usecase.user.GetUserDetailCase;
import com.example.business.usecase.user.RegisterCase;
import com.example.business.usecase.user.ResetPasswordCase;
import com.example.business.usecase.user.UpdateUserCase;
import org.springframework.stereotype.Service;

@Service
public class UserApplicationService {

    private final UserService userService;

    public UserApplicationService(UserService userService) {
        this.userService = userService;
    }

    public RegisterCase.Response register(RegisterCase.Request request) {
        User user = userService.create(request.getName(), request.getEmail(), request.getPassword());

        return RegisterCase.Response.from(user);
    }

    public GetUserDetailCase.Response getDetail(Operator operator) {
        String userId = operator.getUserId();
        return GetUserDetailCase.Response.from(userService.get(userId));
    }

    public UpdateUserCase.Response update(UpdateUserCase.Request request, Operator operator) {
        User updatedUser = userService.update(operator.getUserId(), request.getName(), operator);
        return UpdateUserCase.Response.from(updatedUser);
    }


    public void resetPassword(ResetPasswordCase.Request request, Operator operator) {
        userService.resetPassword(operator.getUserId(), request.getPassword(), operator);
    }
}
