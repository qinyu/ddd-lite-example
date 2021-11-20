package com.example.domain.auth;

import com.example.domain.auth.exception.AuthorizeException;
import com.example.domain.common.BaseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizeContextHolderTest {

    @Test
    void should_fetch_authorize_failed_when_authorize_is_not_exist() {
        //when
        BaseException exception = assertThrows(AuthorizeException.class, AuthorizeContextHolder::getOperator);

        assertEquals("unauthorized", exception.getMessage());
        assertEquals(BaseException.Type.UNAUTHORIZED, exception.getType());
    }

}