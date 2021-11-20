package com.example.admin.rest;

import com.example.admin.service.AdminApplicationService;
import com.example.admin.usecase.authorize.ResetPasswordCase;
import com.example.domain.auth.service.AuthorizeService;
import com.example.domain.user.model.Operator;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admins")
public class AdminController {

    private final AdminApplicationService adminApplicationService;
    private final AuthorizeService authorizeService;

    public AdminController(AdminApplicationService adminApplicationService, AuthorizeService authorizeService) {
        this.adminApplicationService = adminApplicationService;
        this.authorizeService = authorizeService;
    }


    @PutMapping("/password")
    public void resetPassword(@RequestBody ResetPasswordCase.Request request) {
        Operator operator = authorizeService.getOperator();
        adminApplicationService.resetPassword(request, operator);
    }
}
