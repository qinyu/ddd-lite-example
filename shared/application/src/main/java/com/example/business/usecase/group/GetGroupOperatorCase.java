package com.example.business.usecase.group;

import com.example.domain.group.model.GroupMember;
import com.example.domain.group.model.GroupOperator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class GetGroupOperatorCase {
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private String groupId;
        private String userId;
        private GroupMember.Role role;

        public static Response from(GroupOperator groupOperator) {
            return Response.builder()
                    .groupId(groupOperator.getGroupId())
                    .userId(groupOperator.getUserId())
                    .role(groupOperator.getRole())
                    .build();
        }
    }
}
