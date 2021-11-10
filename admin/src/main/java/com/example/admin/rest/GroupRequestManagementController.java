package com.example.admin.rest;

import com.example.admin.service.GroupRequestManagementApplicationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/management/group-requests")
public class GroupRequestManagementController {
    private final GroupRequestManagementApplicationService groupRequestManagementApplicationService;

    public GroupRequestManagementController(GroupRequestManagementApplicationService groupRequestManagementApplicationService) {
        this.groupRequestManagementApplicationService = groupRequestManagementApplicationService;
    }
}
