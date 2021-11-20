package com.example.question.rest;

import com.example.TestBase;
import com.example.business.usecase.group.GetGroupOperatorCase;
import com.example.business.usecase.user.GetUserDetailCase;
import com.example.domain.group.model.Group;
import com.example.domain.group.model.GroupMember;
import com.example.domain.group.model.GroupOperator;
import com.example.domain.question.model.Answer;
import com.example.domain.question.model.Question;
import com.example.domain.question.repository.AnswerRepository;
import com.example.domain.question.repository.QuestionRepository;
import com.example.domain.question.service.QuestionService;
import com.example.domain.user.model.Operator;
import com.example.domain.user.model.User;
import com.example.question.service.GroupClient;
import com.example.question.service.UserClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;

class QuestionControllerTest extends TestBase {
    public static final String MAIN_PATH = "/questions";

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private GroupClient groupClient;
    @Autowired
    private UserClient userClient;

    private Operator getOperator(User user) {
        return Operator.builder().userId(user.getId()).role(user.getRole()).build();
    }

    private GroupOperator getGroupOperator(String groupId, Operator operator) {
        GetGroupOperatorCase.Response response = groupClient.getGroupOperator(groupId, operator);
        return new GroupOperator(response.getGroupId(),
                response.getUserId(),
                response.getRole());
    }

    @Override
    public User prepareUser(String name, String email) {
        return User.builder()
                .name(name)
                .email(email)
                .password("")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .role(User.Role.USER)
                .status(User.Status.NORMAL)
                .id(name + "-id")
                .build();
    }

    private String prepareGroupId() {
        return "group-id";
    }

    private Operator mockDefaultGroupMember(User user) {
        Operator operator = getOperator(user);
        Mockito.when(groupClient.getGroupOperator(Group.DEFAULT, operator)).thenReturn(
                new GetGroupOperatorCase.Response(Group.DEFAULT, operator.getUserId(), GroupMember.Role.NORMAL)
        );
        return operator;
    }

    private Operator mockGroupCreator(String groupId, User user) {
        Operator operator = getOperator(user);
        Mockito.when(groupClient.getGroupOperator(groupId, operator)).thenReturn(
                new GetGroupOperatorCase.Response(groupId, operator.getUserId(), GroupMember.Role.OWNER)
        );
        return operator;
    }

    private Operator mockGroupAdmin(String groupId, User user) {
        Operator operator = getOperator(user);
        Mockito.when(groupClient.getGroupOperator(groupId, operator)).thenReturn(
                new GetGroupOperatorCase.Response(groupId, operator.getUserId(), GroupMember.Role.ADMIN)
        );
        return operator;
    }

    @Test
    void should_create_question() {
        User user = this.prepareUser("anyName", "anyEmail");
        String title = "title";
        String description = "description";

        Response response = givenWithAuthorize(user, Group.DEFAULT)
                .body(new HashMap<String, Object>() {
                    {
                        put("title", title);
                        put("description", description);
                    }
                })
                .when()
                .post(MAIN_PATH);
        response.then().statusCode(201)
                .body("id", isA(String.class))
                .body("title", is(title))
                .body("description", is(description));

        Optional<Question> questionOptional = questionRepository.findOne(Example.of(Question
                .builder()
                .createdBy(user.getId())
                .build()
        ));

        assertThat(questionOptional.isPresent(), is(true));
        assertThat(questionOptional.get().getStatus(), is(Question.Status.OPENED));

    }

    @Test
    void should_get_question_detail() {
        User user = this.prepareUser("anyName", "anyEmail");
        Operator operator = mockDefaultGroupMember(user);
        GroupOperator groupOperator = getGroupOperator(Group.DEFAULT, operator);
        Question question = questionService.create("anyTitle", "anyDescription", groupOperator);

        Response response = givenDefault()
                .when()
                .get(MAIN_PATH + "/" + question.getId());
        response.then().statusCode(200)
                .body("id", is(question.getId()))
                .body("title", is(question.getTitle()))
                .body("description", is(question.getDescription()))
                .body("createdBy", is(question.getCreatedBy()));
    }

    @Test
    void should_query_questions_by_page() {
        User user = this.prepareUser("anyName", "anyEmail");
        Operator operator = mockDefaultGroupMember(user);

        User otherUser = this.prepareUser("anyOtherName", "anyOtherEmail");
        Operator otherOperator = mockDefaultGroupMember(otherUser);

        GroupOperator groupOperator = getGroupOperator(Group.DEFAULT, operator);
        GroupOperator otherGroupOperator = getGroupOperator(Group.DEFAULT, otherOperator);
        Question question0 = questionService.create("anyTitle0", "anyDescription0", groupOperator);
        Question question1 = questionService.create("anyTitle1", "anyDescription1", groupOperator);
        questionService.create("anyTitle2", "anyDescription2", groupOperator);
        Question question3 = questionService.create("anyTitle3", "anyDescription3", otherGroupOperator);

        givenDefault(Group.DEFAULT)
                .param("sort", "createdAt")
                .param("sort", "title")
                .param("size", 2)
                .when()
                .get(MAIN_PATH)
                .then()
                .statusCode(200)
                .body("content.size", is(2))
                .body("content.title", hasItems(question0.getTitle(), question1.getTitle()))
                .body("content.description", hasItems(question0.getDescription(), question1.getDescription()));


        givenDefault(Group.DEFAULT)
                .param("sort", "createdAt")
                .param("sort", "title")
                .param("keyword", "Title1")
                .param("size", 2)
                .when()
                .get(MAIN_PATH)
                .then()
                .statusCode(200)
                .body("content.size", is(1))
                .body("content[0].title", is(question1.getTitle()))
                .body("content[0].description", is(question1.getDescription()));

        givenDefault(Group.DEFAULT)
                .param("sort", "createdAt")
                .param("sort", "title")
                .param("createdBy", otherUser.getId())
                .param("size", 1)
                .when()
                .get(MAIN_PATH)
                .then()
                .statusCode(200)
                .body("content.size", is(1))
                .body("content[0].title", is(question3.getTitle()))
                .body("content[0].description", is(question3.getDescription()));
    }

    @Test
    void should_get_management_questions_by_page() {
        User user = this.prepareUser("anyName", "anyEmail");
        Operator operator = mockDefaultGroupMember(user);

        User otherUser = this.prepareUser("anyOtherName", "anyOtherEmail");
        Operator otherOperator = mockDefaultGroupMember(otherUser);

        Mockito.when(userClient.getAllUsers(Set.of("anyName-id", "otherUserName-id"))).thenReturn(
                new PageImpl<>(List.of(
                        GetUserDetailCase.Response.from(user),
                        GetUserDetailCase.Response.from(otherUser)
                ))
        );

        GroupOperator groupOperator = getGroupOperator(Group.DEFAULT, operator);
        GroupOperator otherGroupOperator = getGroupOperator(Group.DEFAULT, otherOperator);
        Question question0 = questionService.create("anyTitle0", "anyDescription0", groupOperator);
        Question question1 = questionService.create("anyTitle1", "anyDescription1", groupOperator);
        questionService.create("anyTitle2", "anyDescription2", groupOperator);
        Question question3 = questionService.create("anyTitle3", "anyDescription3", otherGroupOperator);


        givenDefault(Group.DEFAULT)
                .param("sort", "createdAt")
                .param("sort", "title")
                .param("size", 2)
                .when()
                .get(MAIN_PATH + "/management")
                .then()
                .statusCode(200)
                .body("content.size", is(2))
                .body("content.title", hasItems(question0.getTitle(), question1.getTitle()))
                .body("content.description", hasItems(question0.getDescription(), question1.getDescription()))
                .body("content[0].creator.id", is(user.getId()))
                .body("content[0].creator.name", is(user.getName()));


        givenDefault(Group.DEFAULT)
                .param("sort", "createdAt")
                .param("sort", "title")
                .param("keyword", "Title1")
                .param("size", 2)
                .when()
                .get(MAIN_PATH + "/management")
                .then()
                .statusCode(200)
                .body("content.size", is(1))
                .body("content[0].title", is(question1.getTitle()))
                .body("content[0].description", is(question1.getDescription()));

        givenDefault(Group.DEFAULT)
                .param("sort", "createdAt")
                .param("sort", "title")
                .param("createdBy", otherUser.getId())
                .param("size", 1)
                .when()
                .get(MAIN_PATH + "/management")
                .then()
                .statusCode(200)
                .body("content.size", is(1))
                .body("content[0].title", is(question3.getTitle()))
                .body("content[0].description", is(question3.getDescription()));
    }

    @Test
    void should_update_question() {
        User user = this.prepareUser("anyName", "anyEmail");
        Operator operator = mockDefaultGroupMember(user);

        GroupOperator groupOperator = getGroupOperator(Group.DEFAULT, operator);
        Question question = questionService.create("anyTitle", "anyDescription", groupOperator);
        String newTitle = "newTitle";
        String newDescription = "newDescription";

        Response response = givenWithAuthorize(user, Group.DEFAULT)
                .body(new HashMap<String, Object>() {
                    {
                        put("title", newTitle);
                        put("description", newDescription);

                    }
                })
                .when()
                .put(MAIN_PATH + "/" + question.getId());
        response.then().statusCode(200)
                .body("id", is(question.getId()))
                .body("title", is(newTitle))
                .body("description", is(newDescription));

        Question updatedQuestion = questionRepository.findById(question.getId()).get();
        assertThat(updatedQuestion.getTitle(), is(newTitle));
        assertThat(updatedQuestion.getDescription(), is(newDescription));
    }

    @Test
    void should_update_question_status_by_creator() {
        User user = this.prepareUser("anyName", "anyEmail");
        Operator operator = mockDefaultGroupMember(user);

        GroupOperator groupOperator = getGroupOperator(Group.DEFAULT, operator);
        Question question = questionService.create("anyTitle", "anyDescription", groupOperator);

        Response response = givenWithAuthorize(user, Group.DEFAULT)
                .body(new HashMap<String, Object>() {
                    {
                        put("status", Question.Status.CLOSED);
                    }
                })
                .when()
                .put(MAIN_PATH + "/" + question.getId() + "/status");
        response.then().statusCode(200)
                .body("id", is(question.getId()))
                .body("status", is(Question.Status.CLOSED.name()));

        Question updatedQuestion = questionRepository.findById(question.getId()).get();
        assertThat(updatedQuestion.getStatus(), is(Question.Status.CLOSED));

        givenWithAuthorize(user, Group.DEFAULT)
                .body(new HashMap<String, Object>() {
                    {
                        put("status", Question.Status.OPENED);
                    }
                })
                .when()
                .put(MAIN_PATH + "/" + question.getId() + "/status");
        updatedQuestion = questionRepository.findById(question.getId()).get();
        assertThat(updatedQuestion.getStatus(), is(Question.Status.OPENED));

    }

    @Test
    void should_update_question_status_by_group_admin_and_not_creator() {
        User user = this.prepareUser("anyName", "anyEmail");
        String groupId = prepareGroupId();
        Operator operator = mockGroupCreator(groupId, user);

        User otherUser = this.prepareUser("otherUserName", "otherUserEmail");
        mockGroupAdmin(groupId, otherUser);

        GroupOperator groupOperator = getGroupOperator(groupId, operator);
        Question question = questionService.create("anyTitle", "anyDescription", groupOperator);

        Response response = givenWithAuthorize(otherUser, groupId)
                .body(new HashMap<String, Object>() {
                    {
                        put("status", Question.Status.CLOSED);
                    }
                })
                .when()
                .put("/questions/" + question.getId() + "/status");
        response.then().statusCode(200)
                .body("id", is(question.getId()))
                .body("status", is(Question.Status.CLOSED.name()));

        Question updatedQuestion = questionRepository.findById(question.getId()).get();
        assertThat(updatedQuestion.getStatus(), is(Question.Status.CLOSED));

        givenWithAuthorize(user, groupId)
                .body(new HashMap<String, Object>() {
                    {
                        put("status", Question.Status.OPENED);
                    }
                })
                .when()
                .put("/questions/" + question.getId() + "/status");
        updatedQuestion = questionRepository.findById(question.getId()).get();
        assertThat(updatedQuestion.getStatus(), is(Question.Status.OPENED));

    }

    @Test
    void should_update_question_status_by_group_owner_and_not_creator() {
        User user = this.prepareUser("anyName", "anyEmail");
        String groupId = prepareGroupId();
        mockGroupCreator(groupId, user);

        User otherUser = this.prepareUser("otherUserName", "otherUserEmail");
        Operator otherOperator = mockGroupAdmin(groupId, otherUser);

        GroupOperator adminGroupOperator = getGroupOperator(groupId, otherOperator);

        Question question = questionService.create("anyTitle", "anyDescription", adminGroupOperator);

        Response response = givenWithAuthorize(user, groupId)
                .body(new HashMap<String, Object>() {
                    {
                        put("status", Question.Status.CLOSED);
                    }
                })
                .when()
                .put(MAIN_PATH + "/" + question.getId() + "/status");
        response.then().statusCode(200)
                .body("id", is(question.getId()))
                .body("status", is(Question.Status.CLOSED.name()));

        Question updatedQuestion = questionRepository.findById(question.getId()).get();
        assertThat(updatedQuestion.getStatus(), is(Question.Status.CLOSED));

        givenWithAuthorize(user, groupId)
                .body(new HashMap<String, Object>() {
                    {
                        put("status", Question.Status.OPENED);
                    }
                })
                .when()
                .put("/questions/" + question.getId() + "/status");
        updatedQuestion = questionRepository.findById(question.getId()).get();
        assertThat(updatedQuestion.getStatus(), is(Question.Status.OPENED));

    }

    @Test
    void should_delete_question_by_creator() {
        User user = this.prepareUser("anyName", "anyEmail");
        Operator operator = mockDefaultGroupMember(user);

        User otherUser = this.prepareUser("otherUserName", "otherUserEmail");
        Operator otherOperator = mockDefaultGroupMember(otherUser);

        GroupOperator groupOperator = getGroupOperator(Group.DEFAULT, operator);
        GroupOperator otherGroupOperator = getGroupOperator(Group.DEFAULT, otherOperator);

        Question question = questionService.create("anyTitle", "anyDescription", groupOperator);
        String questionId = question.getId();

        questionService.addAnswer(questionId, "content0", groupOperator);
        questionService.addAnswer(questionId, "content1", otherGroupOperator);

        Response response = givenWithAuthorize(user, Group.DEFAULT)
                .when()
                .delete(MAIN_PATH + "/" + questionId);
        response.then().statusCode(200);

        assertThat(questionRepository.existsById(questionId), is(false));

        List<Answer> answers = answerRepository.findAll(Example.of(Answer.builder().questionId(questionId).build()));
        assertThat(answers, hasSize(0));
    }

    @Test
    void should_delete_question_by_group_admin_and_not_creator() {
        User user = this.prepareUser("anyName", "anyEmail");
        String groupId = prepareGroupId();
        Operator operator = mockGroupCreator(groupId, user);

        User otherUser = this.prepareUser("otherUserName", "otherUserEmail");
        Operator otherOperator = mockGroupAdmin(groupId, otherUser);

        GroupOperator groupOperator = getGroupOperator(groupId, operator);
        GroupOperator adminOperator = getGroupOperator(groupId, otherOperator);

        Question question = questionService.create("anyTitle", "anyDescription", groupOperator);
        String questionId = question.getId();

        questionService.addAnswer(questionId, "content0", groupOperator);
        questionService.addAnswer(questionId, "content1", adminOperator);

        Response response = givenWithAuthorize(otherUser, groupId)
                .when()
                .delete("/questions/" + questionId);
        response.then().statusCode(200);

        assertThat(questionRepository.existsById(questionId), is(false));

        List<Answer> answers = answerRepository.findAll(Example.of(Answer.builder().questionId(questionId).build()));
        assertThat(answers, hasSize(0));
    }

    @Test
    void should_delete_question_by_group_owner_and_not_creator() {
        User user = this.prepareUser("anyName", "anyEmail");
        String groupId = prepareGroupId();
        Operator operator = mockGroupCreator(groupId, user);

        User otherUser = this.prepareUser("otherUserName", "otherUserEmail");
        Operator otherOperator = mockGroupAdmin(groupId, otherUser);

        GroupOperator groupOperator = getGroupOperator(groupId, operator);
        GroupOperator adminGroupOperator = getGroupOperator(groupId, otherOperator);

        Question question = questionService.create("anyTitle", "anyDescription", adminGroupOperator);
        String questionId = question.getId();

        questionService.addAnswer(questionId, "content0", groupOperator);
        questionService.addAnswer(questionId, "content1", adminGroupOperator);

        Response response = givenWithAuthorize(user, groupId)
                .when()
                .delete("/questions/" + questionId);
        response.then().statusCode(200);

        assertThat(questionRepository.existsById(questionId), is(false));

        List<Answer> answers = answerRepository.findAll(Example.of(Answer.builder().questionId(questionId).build()));
        assertThat(answers, hasSize(0));
    }

    @Test
    void should_create_answer() {
        User user = this.prepareUser("anyName", "anyEmail");
        Operator operator = mockDefaultGroupMember(user);

        GroupOperator groupOperator = getGroupOperator(Group.DEFAULT, operator);
        Question question = questionService.create("anyTitle", "anyDescription", groupOperator);
        String content = "content";

        Response response = givenWithAuthorize(user, Group.DEFAULT)
                .body(new HashMap<String, Object>() {
                    {
                        put("content", content);
                    }
                })
                .when()
                .post(MAIN_PATH + "/" + question.getId() + "/answers");
        response.then().statusCode(201)
                .body("id", isA(String.class))
                .body("content", is(content));

        Optional<Answer> answerOptional = answerRepository.findOne(Example.of(Answer
                .builder()
                .createdBy(user.getId())
                .questionId(question.getId())
                .build()
        ));

        assertThat(answerOptional.isPresent(), is(true));
    }

    @Test
    void should_get_all_answers_by_page() {
        User user = this.prepareUser("anyName", "anyEmail");
        Operator operator = mockDefaultGroupMember(user);

        User otherUser = this.prepareUser("otherUserName", "otherUserEmail");
        Operator otherOperator = mockDefaultGroupMember(otherUser);

        GroupOperator groupOperator = getGroupOperator(Group.DEFAULT, operator);
        GroupOperator otherGroupOperator = getGroupOperator(Group.DEFAULT, otherOperator);

        Question question = questionService.create("anyTitle", "anyDescription", groupOperator);

        Answer answer0 = questionService.addAnswer(question.getId(), "content0", groupOperator);
        Answer answer1 = questionService.addAnswer(question.getId(), "content1", otherGroupOperator);

        Mockito.when(userClient.getAllUsers(Set.of("anyName-id", "otherUserName-id"))).thenReturn(
                new PageImpl<>(List.of(
                        GetUserDetailCase.Response.from(user),
                        GetUserDetailCase.Response.from(otherUser)
                ))
        );

        Response response = givenDefault()
                .param("sort", "createdAt")
                .when()
                .get(MAIN_PATH + "/" + question.getId() + "/answers");


        response.then().statusCode(200)
                .body("content.size", is(2))
                .body("content.content", hasItems(answer0.getContent(), answer1.getContent()))
                .body("content.creator.name", hasItems(user.getName(), otherUser.getName()));

    }

    @Test
    void should_update_answer() {
        User user = this.prepareUser("anyName", "anyEmail");
        Operator operator = mockDefaultGroupMember(user);

        GroupOperator groupOperator = getGroupOperator(Group.DEFAULT, operator);
        Question question = questionService.create("anyTitle", "anyDescription", groupOperator);
        Answer answer = questionService.addAnswer(question.getId(), "content", groupOperator);
        String newContent = "newContent";

        Response response = givenWithAuthorize(user, Group.DEFAULT)
                .body(new HashMap<String, Object>() {
                    {
                        put("content", newContent);
                    }
                })
                .when()
                .put(MAIN_PATH + "/" + question.getId() + "/answers/" + answer.getId());
        response.then().statusCode(200)
                .body("id", isA(String.class))
                .body("content", is(newContent));

        Answer updatedAnswer = answerRepository.findById(answer.getId()).get();

        assertThat(updatedAnswer.getContent(), is(newContent));
    }

    @Test
    void should_delete_answer_by_creator() {
        User user = this.prepareUser("anyName", "anyEmail");
        Operator operator = mockDefaultGroupMember(user);

        GroupOperator groupOperator = getGroupOperator(Group.DEFAULT, operator);
        Question question = questionService.create("anyTitle", "anyDescription", groupOperator);
        Answer answer = questionService.addAnswer(question.getId(), "content", groupOperator);

        Response response = givenWithAuthorize(user, Group.DEFAULT)
                .when()
                .delete(MAIN_PATH + "/" + question.getId() + "/answers/" + answer.getId());
        response.then().statusCode(200);

        assertThat(answerRepository.findById(answer.getId()).isPresent(), is(false));
    }

    @Test
    void should_delete_answer_by_group_admin_and_not_creator() {
        User user = this.prepareUser("anyName", "anyEmail");
        String groupId = prepareGroupId();
        mockGroupCreator(groupId, user);

        User otherUser = this.prepareUser("otherUserName", "otherUserEmail");
        Operator otherOperator = mockGroupAdmin(groupId, otherUser);

        GroupOperator adminGroupOperator = getGroupOperator(groupId, otherOperator);


        Question question = questionService.create("anyTitle", "anyDescription", adminGroupOperator);
        Answer answer = questionService.addAnswer(question.getId(), "content", adminGroupOperator);

        Response response = givenWithAuthorize(otherUser, Group.DEFAULT)
                .when()
                .delete(MAIN_PATH + "/" + question.getId() + "/answers/" + answer.getId());
        response.then().statusCode(200);

        assertThat(answerRepository.findById(answer.getId()).isPresent(), is(false));
    }

    @Test
    void should_delete_answer_by_group_owner_and_not_creator() {
        User user = this.prepareUser("anyName", "anyEmail");
        String groupId = prepareGroupId();
        mockGroupCreator(groupId, user);

        User otherUser = this.prepareUser("otherUserName", "otherUserEmail");
        Operator otherOperator = mockGroupAdmin(groupId, otherUser);

        GroupOperator adminGroupOperator = getGroupOperator(groupId, otherOperator);

        Question question = questionService.create("anyTitle", "anyDescription", adminGroupOperator);
        Answer answer = questionService.addAnswer(question.getId(), "content", adminGroupOperator);

        Response response = givenWithAuthorize(user, groupId)
                .when()
                .delete("/questions/" + question.getId() + "/answers/" + answer.getId());
        response.then().statusCode(200);

        assertThat(answerRepository.findById(answer.getId()).isPresent(), is(false));
    }

}
