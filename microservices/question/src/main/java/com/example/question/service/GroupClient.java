package com.example.question.service;

import com.example.business.usecase.group.GetGroupOperatorCase;
import com.example.domain.user.model.Operator;

public interface GroupClient {

    GetGroupOperatorCase.Response getGroupOperator(String groupId, Operator operator);

}
