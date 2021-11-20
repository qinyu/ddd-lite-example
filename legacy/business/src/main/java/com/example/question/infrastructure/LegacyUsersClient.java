package com.example.question.infrastructure;

import com.example.domain.user.model.User;
import com.example.domain.user.service.UserService;
import com.example.question.service.UsersClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Deprecated
class LegacyUsersClient implements UsersClient {
    private final UserService userService;

    private LegacyUsersClient(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Page<User> getAllUsers(Set<String> userIds) {
        return userService.findAll((root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get(User.Fields.id)).value(userIds), Pageable.unpaged());
    }
}
