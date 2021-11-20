package com.example.admin.rest;

import com.example.admin.service.QuestionManagementApplicationService;
import com.example.admin.usecase.question.GetQuestionsCase;
import com.example.admin.usecase.question.UpdateQuestionStatusCase;
import com.example.domain.auth.AuthorizeContextHolder;
import com.example.domain.group.GroupContextHolder;
import com.example.domain.user.model.Operator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/management/questions")
public class QuestionManagementController {

    private final QuestionManagementApplicationService questionManagementApplicationService;

    public QuestionManagementController(QuestionManagementApplicationService questionManagementApplicationService) {
        this.questionManagementApplicationService = questionManagementApplicationService;
    }

    @GetMapping
    public Page<GetQuestionsCase.Response> getQuestions(@RequestParam(required = false) String keyword,
                                                        Pageable pageable) {
        return questionManagementApplicationService.getQuestions(keyword, pageable);
    }

    @PutMapping("/{id}/status")
    public UpdateQuestionStatusCase.Response updateQuestionStatus(@PathVariable String id,
                                                                  @RequestBody
                                                                  @Valid UpdateQuestionStatusCase.Request request) {
        Operator operator = AuthorizeContextHolder.getOperator();
        String groupId = GroupContextHolder.getContext();
        return questionManagementApplicationService.updateStatus(id, request, groupId, operator);
    }

    @DeleteMapping("/{id}")
    public void deleteQuestion(@PathVariable String id) {
        Operator operator = AuthorizeContextHolder.getOperator();
        String groupId = GroupContextHolder.getContext();
        questionManagementApplicationService.delete(id, groupId, operator);
    }
}
