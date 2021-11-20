package com.example.business.rest;

import com.example.business.service.QuestionApplicationService;
import com.example.business.usecase.question.CreateAnswerCase;
import com.example.business.usecase.question.CreateQuestionCase;
import com.example.business.usecase.question.GetAnswerCase;
import com.example.business.usecase.question.GetManagementQuestionCase;
import com.example.business.usecase.question.GetQuestionCase;
import com.example.business.usecase.question.GetQuestionDetailCase;
import com.example.business.usecase.question.UpdateAnswerCase;
import com.example.business.usecase.question.UpdateQuestionCase;
import com.example.business.usecase.question.UpdateQuestionStatusCase;
import com.example.domain.auth.service.AuthorizeService;
import com.example.domain.group.GroupContextHolder;
import com.example.domain.user.model.Operator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionApplicationService questionApplicationService;
    private final AuthorizeService authorizeService;

    public QuestionController(QuestionApplicationService questionApplicationService,
                              AuthorizeService authorizeService) {
        this.questionApplicationService = questionApplicationService;
        this.authorizeService = authorizeService;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public CreateQuestionCase.Response createQuestion(@RequestBody @Valid CreateQuestionCase.Request request) {
        String groupId = GroupContextHolder.getContext();
        Operator operator = authorizeService.getOperator();
        return questionApplicationService.create(request, groupId, operator);
    }

    @GetMapping
    public Page<GetQuestionCase.Response> getQuestionsByPage(@RequestParam(required = false) String keyword,
                                                             @RequestParam(required = false) String createdBy,
                                                             @PageableDefault Pageable pageable) {
        String groupId = GroupContextHolder.getContext();
        return questionApplicationService.getByPage(groupId, keyword, createdBy, pageable);
    }

    @GetMapping("/{id}")
    public GetQuestionDetailCase.Response getQuestionDetail(@PathVariable String id) {
        return questionApplicationService.getDetail(id);
    }

    @PostMapping("/{id}/answers")
    @ResponseStatus(CREATED)
    public CreateAnswerCase.Response createAnswer(@PathVariable String id,
                                                  @RequestBody @Valid CreateAnswerCase.Request request) {
        String groupId = GroupContextHolder.getContext();
        Operator operator = authorizeService.getOperator();
        return questionApplicationService.createAnswer(id, request, groupId, operator);
    }

    @GetMapping("/{id}/answers")
    public Page<GetAnswerCase.Response> getAnswersByPage(@PathVariable String id,
                                                         @PageableDefault Pageable pageable) {
        return questionApplicationService.getAnswersByPage(id, pageable);
    }

    @GetMapping("/management")
    public Page<GetManagementQuestionCase.Response> getManagementQuestions(@RequestParam(required = false)
                                                                                   String keyword,
                                                                           @RequestParam(required = false)
                                                                                   String createdBy,
                                                                           @PageableDefault Pageable pageable) {
        String groupId = GroupContextHolder.getContext();
        return questionApplicationService.getManagementQuestions(groupId, keyword, createdBy, pageable);
    }

    @PutMapping("/{id}")
    public UpdateQuestionCase.Response updateQuestion(@PathVariable String id,
                                                      @RequestBody @Valid UpdateQuestionCase.Request request) {
        Operator operator = authorizeService.getOperator();
        String groupId = GroupContextHolder.getContext();
        return questionApplicationService.update(id, request, groupId, operator);
    }

    @PutMapping("/{id}/status")
    public UpdateQuestionStatusCase.Response updateQuestionStatus(@PathVariable String id,
                                                                  @RequestBody
                                                                  @Valid UpdateQuestionStatusCase.Request request) {
        Operator operator = authorizeService.getOperator();
        String groupId = GroupContextHolder.getContext();
        return questionApplicationService.updateStatus(id, request, groupId, operator);
    }

    @DeleteMapping("/{id}")
    public void deleteQuestion(@PathVariable String id) {
        Operator operator = authorizeService.getOperator();
        String groupId = GroupContextHolder.getContext();
        questionApplicationService.delete(id, groupId, operator);
    }

    @PutMapping("/{id}/answers/{answerId}")
    public UpdateAnswerCase.Response createAnswer(@PathVariable String id,
                                                  @PathVariable String answerId,
                                                  @RequestBody @Valid UpdateAnswerCase.Request request) {
        Operator operator = authorizeService.getOperator();
        String groupId = GroupContextHolder.getContext();
        return questionApplicationService.updateAnswer(id, answerId, request, groupId, operator);
    }

    @DeleteMapping("/{id}/answers/{answerId}")
    public void deleteAnswer(@PathVariable String id,
                             @PathVariable String answerId) {
        Operator operator = authorizeService.getOperator();
        String groupId = GroupContextHolder.getContext();
        questionApplicationService.deleteAnswer(id, answerId, groupId, operator);
    }
}
