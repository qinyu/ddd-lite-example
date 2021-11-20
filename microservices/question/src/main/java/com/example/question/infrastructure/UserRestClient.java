package com.example.question.infrastructure;

import com.example.business.usecase.user.GetUserDetailCase;
import com.example.domain.auth.AuthorizeContextHolder;
import com.example.question.service.UserClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.UnknownHostException;
import java.util.Set;

@Component
public class UserRestClient implements UserClient {
    private final WebClient webClient;

    private UserRestClient(WebClient.Builder builder, EnvUtil envUtil) throws UnknownHostException {
        this.webClient = builder.baseUrl(envUtil.getServerUrl()).build();
    }

    @Override
    public Page<GetUserDetailCase.Response> getAllUsers(Set<String> userIds) {
        return webClient.get()
                .uri("/users?ids={ids}", String.join(",", userIds))
                .header("Authorization", "Bearer " + AuthorizeContextHolder.getContext().getId())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Page<GetUserDetailCase.Response>>() {
                })
                .block();
    }

}
