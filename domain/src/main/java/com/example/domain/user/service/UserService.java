package com.example.domain.user.service;

import com.example.domain.user.exception.UserException;
import com.example.domain.user.model.Operator;
import com.example.domain.user.model.User;
import com.example.domain.user.repository.UserRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository repository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = repository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User get(String id) {
        return _get(id);
    }

    private User _get(String id) {
        return userRepository.findById(id).orElseThrow(UserException::notFound);
    }

    public User get(Example<User> example) {
        return userRepository.findOne(example).orElseThrow(UserException::notFound);
    }

    public Page<User> findAll(Specification<User> spec, Pageable pageable) {
        return userRepository.findAll(spec, pageable);
    }

    public List<User> findAll(Specification<User> spec) {
        return userRepository.findAll(spec);
    }

    public User create(String name, String email, String password) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .role(User.Role.USER)
                .status(User.Status.NORMAL)
                .build();
        validateConflicted(user);
        return userRepository.save(user);
    }

    public User update(String id, String name, Operator operator) {
        User user = _get(id);

        if (!id.equals(operator.getUserId())) {
            throw UserException.noPermissionUpdate();
        }

        user.setName(name);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    public User resetPassword(String id, String password, Operator operator) {
        User user = _get(id);

        if (!id.equals(operator.getUserId())) {
            throw UserException.noPermissionUpdate();
        }

        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    public User updateStatus(String id, User.Status status, Operator operator) {
        if (!operator.getRole().equals(User.Role.ADMIN)) {
            throw UserException.noPermissionUpdate();
        }

        User user = _get(id);
        user.setStatus(status);
        user.setUpdatedAt(Instant.now());

        return userRepository.save(user);
    }

    private void validateConflicted(User user) {
        if (userRepository.exists(Example.of(User.builder().email(user.getEmail()).build()))) {
            throw UserException.emailConflicted();
        }
    }
}
