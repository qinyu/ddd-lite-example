package com.example.business.rest;

import com.example.business.service.UserApplicationService;
import com.example.business.usecase.user.GetUserDetailCase;
import com.example.business.usecase.user.RegisterCase;
import com.example.business.usecase.user.ResetPasswordCase;
import com.example.business.usecase.user.UpdateUserCase;
import com.example.domain.auth.AuthorizeContextHolder;
import com.example.domain.user.model.Operator;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserApplicationService applicationService;

    public UserController(UserApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public RegisterCase.Response register(@RequestBody @Valid RegisterCase.Request request) {
        return applicationService.register(request);
    }

    @GetMapping("/me")
    public GetUserDetailCase.Response getDetail() {
        Operator operator = AuthorizeContextHolder.getOperator();
        return applicationService.getDetail(operator);
    }

    @PutMapping("/me")
    public UpdateUserCase.Response update(@RequestBody @Valid UpdateUserCase.Request request) {
        Operator operator = AuthorizeContextHolder.getOperator();
        return applicationService.update(request, operator);
    }

    @PutMapping("/me/password")
    public void resetPassword(@RequestBody @Valid ResetPasswordCase.Request request) {
        Operator operator = AuthorizeContextHolder.getOperator();
        applicationService.resetPassword(request, operator);
    }
}
