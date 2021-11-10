package com.example.business.rest;

import com.example.business.service.GroupRequestApplicationService;
import com.example.business.usecase.group.CreateGroupRequestCase;
import com.example.domain.auth.service.AuthorizeService;
import com.example.domain.user.model.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/group-requests")
public class GroupRequestController {

    private final GroupRequestApplicationService applicationService;

    private final AuthorizeService authorizeService;

    public GroupRequestController(GroupRequestApplicationService applicationService, AuthorizeService authorizeService) {
        this.applicationService = applicationService;
        this.authorizeService = authorizeService;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public CreateGroupRequestCase.Response createGroupRequest(@RequestBody @Valid CreateGroupRequestCase.Request request) {
        Operator operator = authorizeService.getOperator();
        return applicationService.createGroup(request, operator);
    }
}
