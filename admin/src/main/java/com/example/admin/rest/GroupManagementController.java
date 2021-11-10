package com.example.admin.rest;

import com.example.admin.service.GroupManagementApplicationService;
import com.example.admin.usecases.group.CreateGroupCase;
import com.example.admin.usecases.group.GetGroupsCase;
import com.example.domain.auth.service.AuthorizeService;
import com.example.domain.user.model.Operator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/management/groups")
public class GroupManagementController {

    private final GroupManagementApplicationService groupManagementApplicationService;
    private final AuthorizeService authorizeService;

    public GroupManagementController(GroupManagementApplicationService groupManagementApplicationService,
                                     AuthorizeService authorizeService) {
        this.groupManagementApplicationService = groupManagementApplicationService;
        this.authorizeService = authorizeService;
    }

    @GetMapping
    public Page<GetGroupsCase.Response> getGroups(@RequestParam(required = false) String keyword,
                                                     Pageable pageable) {
        return groupManagementApplicationService.getGroups(keyword, pageable);
    }

    @PostMapping
    public CreateGroupCase.Response createGroup(@RequestBody @Valid CreateGroupCase.Request request) {
        Operator operator = authorizeService.getOperator();

        return groupManagementApplicationService.creteGroup(request, operator);
    }
}
