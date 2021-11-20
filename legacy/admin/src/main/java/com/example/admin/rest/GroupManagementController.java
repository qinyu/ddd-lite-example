package com.example.admin.rest;

import com.example.admin.service.GroupManagementApplicationService;
import com.example.admin.usecase.group.CreateGroupCase;
import com.example.admin.usecase.group.GetGroupsCase;
import com.example.domain.auth.AuthorizeContextHolder;
import com.example.domain.user.model.Operator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/management/groups")
public class GroupManagementController {

    private final GroupManagementApplicationService groupManagementApplicationService;

    public GroupManagementController(GroupManagementApplicationService groupManagementApplicationService) {
        this.groupManagementApplicationService = groupManagementApplicationService;
    }

    @GetMapping
    public Page<GetGroupsCase.Response> getGroups(@RequestParam(required = false) String keyword,
                                                     Pageable pageable) {
        return groupManagementApplicationService.getGroups(keyword, pageable);
    }

    @PostMapping
    public CreateGroupCase.Response createGroup(@RequestBody @Valid CreateGroupCase.Request request) {
        Operator operator = AuthorizeContextHolder.getOperator();

        return groupManagementApplicationService.creteGroup(request, operator);
    }
}
