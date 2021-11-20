package com.example.business.rest;

import com.example.business.service.GroupRequestApplicationService;
import com.example.business.usecase.group.CreateGroupRequestCase;
import com.example.domain.auth.AuthorizeContextHolder;
import com.example.domain.user.model.Operator;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/group-requests")
public class GroupRequestController {

    private final GroupRequestApplicationService applicationService;

    public GroupRequestController(GroupRequestApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public CreateGroupRequestCase.Response createGroupRequest(@RequestBody @Valid CreateGroupRequestCase.Request request) {
        Operator operator = AuthorizeContextHolder.getOperator();
        return applicationService.createGroup(request, operator);
    }
}
