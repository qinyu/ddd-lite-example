package com.example.admin.rest;

import com.example.admin.service.AdminAuthorizeApplicationService;
import com.example.admin.usecases.authorize.LoginCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/authorizes/admin")
public class AdminAuthorizeController {

    private final AdminAuthorizeApplicationService adminAuthorizeApplicationService;

    public AdminAuthorizeController(AdminAuthorizeApplicationService adminAuthorizeApplicationService) {
        this.adminAuthorizeApplicationService = adminAuthorizeApplicationService;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public LoginCase.Response login(@RequestBody LoginCase.Request request) {
        return adminAuthorizeApplicationService.login(request);
    }

    @DeleteMapping
    public void logout() {
        adminAuthorizeApplicationService.logout();
    }
}
