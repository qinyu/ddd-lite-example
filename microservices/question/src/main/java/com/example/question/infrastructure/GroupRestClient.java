package com.example.question.infrastructure;

import com.example.business.usecase.group.GetGroupOperatorCase;
import com.example.domain.auth.AuthorizeContextHolder;
import com.example.domain.user.model.Operator;
import com.example.question.service.GroupClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.UnknownHostException;

@Component
class GroupRestClient implements GroupClient {
    private final WebClient webClient;

    private GroupRestClient(WebClient.Builder builder, EnvUtil envUtil) throws UnknownHostException {
        this.webClient = builder.baseUrl(envUtil.getServerUrl()).build();
    }

    @Override
    public GetGroupOperatorCase.Response getGroupOperator(String groupId, Operator operator) {
        return webClient.get()
                .uri("/groups/{id}/members/{userId}", groupId, operator.getUserId())
                .header("Authorization", "Bearer " + AuthorizeContextHolder.getContext().getId())
                .retrieve()
                .bodyToMono(GetGroupOperatorCase.Response.class)
                .block();
    }

}
