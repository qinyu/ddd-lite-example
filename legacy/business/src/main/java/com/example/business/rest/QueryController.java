package com.example.business.rest;

import com.example.business.service.QueryApplicationService;
import com.example.business.usecase.query.QueryAnswersCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/query")
public class QueryController {

    private final QueryApplicationService applicationService;

    public QueryController(QueryApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/answers")
    public Page<QueryAnswersCase.Response> queryAnswers(@RequestParam String userId,
                                                        @PageableDefault Pageable pageable) {
        return applicationService.queryAnswers(userId, pageable);
    }

}
