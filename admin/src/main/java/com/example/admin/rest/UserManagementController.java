package com.example.admin.rest;

import com.example.admin.service.UserManagementApplicationService;
import com.example.admin.usecases.user.CreateUserCase;
import com.example.admin.usecases.user.GetUserDetailCase;
import com.example.admin.usecases.user.GetUsersCase;
import com.example.admin.usecases.user.UpdateUserStatusCase;
import com.example.domain.auth.service.AuthorizeService;
import com.example.domain.user.model.Operator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/management/users")
public class UserManagementController {

    private final UserManagementApplicationService userManagementApplicationService;
    private final AuthorizeService authorizeService;

    public UserManagementController(UserManagementApplicationService userManagementApplicationService,
                                    AuthorizeService authorizeService) {
        this.userManagementApplicationService = userManagementApplicationService;
        this.authorizeService = authorizeService;
    }

    @GetMapping
    public Page<GetUsersCase.Response> getUsers(@RequestParam(required = false) String keyword,
                                                Pageable pageable) {
        return userManagementApplicationService.getUsers(keyword, pageable);
    }

    @GetMapping("/{id}")
    public GetUserDetailCase.Response getUserDetail(@PathVariable("id") String id) {
        return userManagementApplicationService.getUserDetail(id);
    }

    @PostMapping()
    @ResponseStatus(CREATED)
    public CreateUserCase.Response createUser(@RequestBody @Valid CreateUserCase.Request request) {
        Operator operator = authorizeService.getOperator();
        return userManagementApplicationService.createUser(request, operator);
    }

    @PutMapping("/{id}/status")
    public UpdateUserStatusCase.Response updateUserStatus(@PathVariable("id") String id,
                                                          @RequestBody @Valid UpdateUserStatusCase.Request request) {
        Operator operator = authorizeService.getOperator();
        return userManagementApplicationService.updateUserStatus(id, request, operator);
    }
}
