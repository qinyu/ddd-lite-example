package com.example.question.service;

import com.example.business.usecase.user.GetUserDetailCase;
import org.springframework.data.domain.Page;

import java.util.Set;

public interface UserClient {
    Page<GetUserDetailCase.Response> getAllUsers(Set<String> userIds);
}
