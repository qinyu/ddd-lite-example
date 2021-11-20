package com.example.question.infrastructure;

import com.example.domain.group.model.Group;
import com.example.domain.group.model.GroupMember;
import com.example.domain.group.model.GroupOperator;
import com.example.domain.group.service.GroupService;
import com.example.domain.user.model.Operator;
import com.example.question.service.GroupClient;
import org.springframework.stereotype.Component;

@Component
@Deprecated
class LegacyGroupClient implements GroupClient {
    private final GroupService groupService;

    private LegacyGroupClient(GroupService groupService) {
        this.groupService = groupService;
    }

    @Override
    public GroupOperator getGroupOperator(String groupId, Operator operator) {
        return groupService.getOperator(groupId, operator);
    }

    @Override
    public GroupMember changeMemberRole(String id, String userId, GroupMember.Role admin, Operator operator) {
        return groupService.changeMemberRole(id, userId, admin, operator);
    }

    @Override
    public Group create(String anyGroupName, String s, Operator operator) {
        return groupService.create(anyGroupName, s, operator);
    }

    @Override
    public GroupMember addNormalMember(String id, Operator operator) {
        return groupService.addNormalMember(id, operator);
    }
}
