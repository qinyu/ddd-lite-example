package com.example.business.rest;

import com.example.business.service.GroupApplicationService;
import com.example.business.usecase.group.*;
import com.example.domain.auth.AuthorizeContextHolder;
import com.example.domain.group.model.GroupOperator;
import com.example.domain.user.model.Operator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupApplicationService applicationService;

    public GroupController(GroupApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public CreateGroupCase.Response createGroup(@RequestBody @Valid CreateGroupCase.Request request) {
        Operator operator = AuthorizeContextHolder.getOperator();
        return applicationService.createGroup(request, operator);
    }

    @GetMapping("/{id}")
    public GetGroupDetailCase.Response getGroupDetail(@PathVariable String id) {
        return applicationService.getGroupDetail(id);
    }

    @GetMapping()
    public Page<GetGroupCase.Response> getGroupsByPage(@PageableDefault Pageable pageable) {
        return applicationService.getGroupsByPage(pageable);
    }

    @GetMapping("/mine")
    public Page<GetMyGroupCase.Response> getMyGroupsByPage(@PageableDefault Pageable pageable) {
        Operator operator = AuthorizeContextHolder.getOperator();
        return applicationService.getMineGroupsByPage(pageable, operator);
    }

    @PutMapping("/{id}")
    public UpdateGroupCase.Response updateGroup(@PathVariable String id,
                                                @RequestBody @Valid UpdateGroupCase.Request request) {
        Operator operator = AuthorizeContextHolder.getOperator();
        return applicationService.updateGroup(id, request, operator);
    }

    @GetMapping("/{id}/members")
    public Page<GetGroupMemberCase.Response> getGroupMembers(@PathVariable String id, Pageable pageable) {
        return applicationService.getGroupMembers(id, pageable);
    }

    @GetMapping("/{id}/members/management")
    public Page<GetGroupMemberCase.Response> getGroupManagementMembers(@PathVariable String id, Pageable pageable) {
        return applicationService.getGroupManagementMembers(id, pageable);
    }

    @PostMapping("/{id}/members/me")
    @ResponseStatus(CREATED)
    public JoinGroupCase.Response joinGroup(@PathVariable String id) {
        Operator operator = AuthorizeContextHolder.getOperator();
        return applicationService.joinGroup(id, operator);
    }

    @DeleteMapping("/{id}/members/me")
    public void exitGroup(@PathVariable String id) {
        Operator operator = AuthorizeContextHolder.getOperator();
        applicationService.exitGroup(id, operator);
    }

    @PutMapping("/{id}/members/{userId}")
    public UpdateGroupMemberCase.Response updateMember(@PathVariable String id,
                                                       @PathVariable String userId,
                                                       @RequestBody @Valid UpdateGroupMemberCase.Request request) {
        Operator operator = AuthorizeContextHolder.getOperator();
        return applicationService.updateMember(id, userId, request, operator);
    }

    @PutMapping("/{id}/owner")
    public ChangeGroupOwnerCase.Response changeOwner(@PathVariable String id,
                                                     @RequestBody @Valid ChangeGroupOwnerCase.Request request) {
        Operator operator = AuthorizeContextHolder.getOperator();
        return applicationService.changeOwner(id, request, operator);
    }

    @DeleteMapping("/{id}/members/{userId}")
    public void removeMember(@PathVariable String id, @PathVariable String userId) {
        Operator operator = AuthorizeContextHolder.getOperator();
        applicationService.removeMember(id, userId, operator);
    }

    @GetMapping("/{id}/members/{userId}")
    public GetGroupOperatorCase.Response getGroupOperator(@PathVariable String id, @PathVariable String userId) {
        return GetGroupOperatorCase.Response.from(applicationService.getOperator(id, userId));
    }

}
