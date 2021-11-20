package com.example.domain.auth;

import com.example.domain.auth.exception.AuthorizeException;
import com.example.domain.auth.model.Authorize;
import com.example.domain.user.model.Operator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthorizeContextHolder {
    private static final ThreadLocal<Authorize> contextHolder = new ThreadLocal<>();

    public static Authorize getContext() {
        return contextHolder.get();
    }

    public static void setContext(Authorize context) {
        contextHolder.set(context);
    }

    public static Operator getOperator() {
        Authorize authorize = getContext();
        if (authorize == null || authorize.getUserId() == null) {
            throw AuthorizeException.Unauthorized();
        }

        return Operator.builder().userId(authorize.getUserId()).role(authorize.getRole()).build();
    }
}
