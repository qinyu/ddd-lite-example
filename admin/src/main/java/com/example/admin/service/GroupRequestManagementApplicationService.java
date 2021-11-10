package com.example.admin.service;

import com.example.domain.group.service.GroupRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupRequestManagementApplicationService {

    private final GroupRequestService groupRequestService;

    public GroupRequestManagementApplicationService(GroupRequestService groupRequestService) {
        this.groupRequestService = groupRequestService;
    }
}
