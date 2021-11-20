package com.example.question.config;

import com.example.question.service.GroupClient;
import com.example.question.service.UserClient;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class TestConfiguration {

    @Bean
    @Primary
    public UserClient userClient() {
        return Mockito.mock(UserClient.class);
    }

    @Bean
    @Primary
    public GroupClient groupClient() {
        return Mockito.mock(GroupClient.class);
    }
}
