package com.example.question.service;

import com.example.domain.user.model.User;
import org.springframework.data.domain.Page;

import java.util.Set;

public interface UsersClient {
    Page<User> getAllUsers(Set<String> userIds);
}
