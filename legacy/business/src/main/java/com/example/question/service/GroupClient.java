package com.example.question.service;

import com.example.domain.group.model.Group;
import com.example.domain.group.model.GroupMember;
import com.example.domain.group.model.GroupOperator;
import com.example.domain.user.model.Operator;

public interface GroupClient {
    GroupOperator getGroupOperator(String groupId, Operator operator);
    GroupMember changeMemberRole(String id, String userId, GroupMember.Role admin, Operator operator);

    Group create(String anyGroupName, String s, Operator operator);

    GroupMember addNormalMember(String id, Operator operator);
}
