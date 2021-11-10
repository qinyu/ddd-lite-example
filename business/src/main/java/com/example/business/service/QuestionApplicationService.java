package com.example.business.service;

import com.example.business.usecase.question.CreateAnswerCase;
import com.example.business.usecase.question.CreateQuestionCase;
import com.example.business.usecase.question.GetAnswerCase;
import com.example.business.usecase.question.GetManagementQuestionCase;
import com.example.business.usecase.question.GetQuestionCase;
import com.example.business.usecase.question.GetQuestionDetailCase;
import com.example.business.usecase.question.UpdateAnswerCase;
import com.example.business.usecase.question.UpdateQuestionCase;
import com.example.business.usecase.question.UpdateQuestionStatusCase;
import com.example.domain.group.model.GroupOperator;
import com.example.domain.group.service.GroupService;
import com.example.domain.question.model.Answer;
import com.example.domain.question.model.Question;
import com.example.domain.question.service.QuestionService;
import com.example.domain.user.model.Operator;
import com.example.domain.user.model.User;
import com.example.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.business.common.UserCriteria.getUsersIn;
import static java.util.stream.Collectors.toMap;

@Service
public class QuestionApplicationService {

    private final QuestionService questionService;
    private final GroupService groupService;
    private final UserService userService;

    public QuestionApplicationService(QuestionService questionService,
                                      GroupService groupService,
                                      UserService userService) {
        this.questionService = questionService;
        this.groupService = groupService;
        this.userService = userService;
    }

    public CreateQuestionCase.Response create(CreateQuestionCase.Request request, String groupId, Operator operator) {
        GroupOperator groupOperator = groupService.getOperator(groupId, operator);

        Question question = questionService.create(request.getTitle(), request.getDescription(), groupOperator);

        return CreateQuestionCase.Response.from(question);
    }

    public GetQuestionDetailCase.Response getDetail(String id) {
        Question question = questionService.get(id);

        return GetQuestionDetailCase.Response.from(question);
    }

    public Page<GetQuestionCase.Response> getByPage(String groupId, String keyword, String createdBy,
                                                    Pageable pageable) {
        Specification<Question> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(criteriaBuilder.equal(root.get(Question.Fields.groupId), groupId));

            if (keyword != null) {
                predicateList.add(criteriaBuilder.like(root.get(Question.Fields.title), "%" + keyword + "%"));
            }

            if (createdBy != null) {
                predicateList.add(criteriaBuilder.equal(root.get(Question.Fields.createdBy), createdBy));
            }

            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };

        Page<Question> questionPage = questionService.findAll(specification, pageable);

        return questionPage.map(GetQuestionCase.Response::from);
    }

    public Page<GetManagementQuestionCase.Response> getManagementQuestions(String groupId,
                                                                           String keyword,
                                                                           String createdBy,
                                                                           Pageable pageable) {
        Specification<Question> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(criteriaBuilder.equal(root.get(Question.Fields.groupId), groupId));

            if (keyword != null) {
                predicateList.add(criteriaBuilder.like(root.get(Question.Fields.title), "%" + keyword + "%"));
            }

            if (createdBy != null) {
                predicateList.add(criteriaBuilder.equal(root.get(Question.Fields.createdBy), createdBy));
            }

            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };

        Page<Question> page = questionService.findAll(specification, pageable);

        Set<String> userIds = page.getContent().stream().map(Question::getCreatedBy).collect(Collectors.toSet());
        Map<String, User> userMap = userService.findAll(getUsersIn(userIds), Pageable.unpaged()).getContent().stream()
                .collect(toMap(User::getId, Function.identity()));
        return page.map(question -> GetManagementQuestionCase.Response
                .from(question, userMap.get(question.getCreatedBy())));
    }

    public UpdateQuestionCase.Response update(String id, UpdateQuestionCase.Request request, String groupId,
                                              Operator operator) {
        GroupOperator groupOperator = groupService.getOperator(groupId, operator);

        Question question =
                questionService.update(id, request.getTitle(), request.getDescription(), groupOperator);

        return UpdateQuestionCase.Response.from(question);
    }

    public UpdateQuestionStatusCase.Response updateStatus(String id, UpdateQuestionStatusCase.Request request,
                                                          String groupId, Operator operator) {
        GroupOperator groupOperator = groupService.getOperator(groupId, operator);

        Question question = questionService.updateStatus(id, request.getStatus(), groupOperator);

        return UpdateQuestionStatusCase.Response.from(question);
    }

    public void delete(String id, String groupId, Operator operator) {
        GroupOperator groupOperator = groupService.getOperator(groupId, operator);

        questionService.delete(id, groupOperator);
    }

    public CreateAnswerCase.Response createAnswer(String id, CreateAnswerCase.Request request, String groupId,
                                                  Operator operator) {
        GroupOperator groupOperator = groupService.getOperator(groupId, operator);

        Answer answer = questionService.addAnswer(id, request.getContent(), groupOperator);

        return CreateAnswerCase.Response.from(answer);
    }

    public Page<GetAnswerCase.Response> getAnswersByPage(String id, Pageable pageable) {
        Specification<Answer> specification = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Answer.Fields.questionId), id);
        Page<Answer> page = questionService.findAllAnswers(specification, pageable);

        Set<String> userIds = page.getContent().stream().map(Answer::getCreatedBy).collect(Collectors.toSet());
        Map<String, User> userMap = userService.findAll(getUsersIn(userIds), Pageable.unpaged()).getContent().stream()
                .collect(toMap(User::getId, Function.identity()));
        return page.map(answer -> GetAnswerCase.Response.from(answer, userMap.get(answer.getCreatedBy())));
    }

    public UpdateAnswerCase.Response updateAnswer(String id, String answerId, UpdateAnswerCase.Request request,
                                                  String groupId, Operator operator) {
        GroupOperator groupOperator = groupService.getOperator(groupId, operator);

        Answer answer = questionService.updateAnswer(id, answerId, request.getContent(), groupOperator);

        return UpdateAnswerCase.Response.from(answer);
    }

    public void deleteAnswer(String id, String answerId, String groupId, Operator operator) {
        GroupOperator groupOperator = groupService.getOperator(groupId, operator);

        questionService.deleteAnswer(id, answerId, groupOperator);
    }
}
