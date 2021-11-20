package com.example.domain.question.service;

import com.example.domain.group.model.GroupMember;
import com.example.domain.group.model.GroupOperator;
import com.example.domain.question.exception.QuestionException;
import com.example.domain.question.model.Answer;
import com.example.domain.question.model.Question;
import com.example.domain.question.repository.AnswerRepository;
import com.example.domain.question.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public QuestionService(QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    public Question get(String id) {
        return _get(id);
    }

    private Question _get(String id) {
        return questionRepository.findById(id).orElseThrow(QuestionException::notFound);
    }

    public Page<Question> findAll(Specification<Question> spec, Pageable pageable) {
        return questionRepository.findAll(spec, pageable);
    }

    public List<Question> findAll(Specification<Question> spec) {
        return questionRepository.findAll(spec);
    }

    public Question create(String title, String description, GroupOperator operator) {
        Question question = Question.builder()
                .title(title)
                .description(description)
                .groupId(operator.getGroupId())
                .status(Question.Status.OPENED)
                .createdBy(operator.getUserId())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return questionRepository.save(question);
    }

    public Question update(String id, String title, String description, GroupOperator operator) {
        Question question = this._get(id);

        if (!question.getCreatedBy().equals(operator.getUserId())) {
            throw QuestionException.forbidden();
        }

        question.setTitle(title);
        question.setDescription(description);
        question.setUpdatedAt(Instant.now());
        return questionRepository.save(question);
    }

    public Question updateStatus(String id, Question.Status status, GroupOperator operator) {
        Question question = this._get(id);

        if (operator.getRole().compareTo(GroupMember.Role.ADMIN) < 0 && !question.getCreatedBy().equals(operator.getUserId())) {
            throw QuestionException.forbidden();
        }

        question.setStatus(status);
        question.setUpdatedAt(Instant.now());
        return questionRepository.save(question);
    }

    public void delete(String id, GroupOperator operator) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (!optionalQuestion.isPresent()) {
            return;
        }

        Question question = optionalQuestion.get();
        if (operator.getRole().compareTo(GroupMember.Role.ADMIN) < 0 && !question.getCreatedBy().equals(operator.getUserId())) {
            throw QuestionException.forbidden();
        }

        answerRepository.deleteAll(question.getAnswers());

        questionRepository.deleteById(id);
    }

    private Answer _getAnswer(String answerId) {
        return answerRepository.findById(answerId).orElseThrow(QuestionException::answerNotFound);
    }

    public Answer addAnswer(String id, String content, GroupOperator operator) {
        Question question = _get(id);

        Answer answer = Answer.builder()
                .questionId(id)
                .content(content)
                .createdBy(operator.getUserId())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return answerRepository.save(answer);
    }

    public Page<Answer> findAllAnswers(Specification<Answer> spec, Pageable pageable) {
        return answerRepository.findAll(spec, pageable);
    }

    public Answer updateAnswer(String id, String answerId, String content, GroupOperator operator) {
        Question question = _get(id);

        Answer answer = _getAnswer(answerId);
        if (!answer.getCreatedBy().equals(operator.getUserId())) {
            throw QuestionException.answerForbidden();
        }

        answer.setContent(content);
        answer.setUpdatedAt(Instant.now());

        return answerRepository.save(answer);
    }

    public void deleteAnswer(String id, String answerId, GroupOperator operator) {
        Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
        if (!optionalAnswer.isPresent()) {
            return;
        }
        if (operator.getRole().compareTo(GroupMember.Role.ADMIN) < 0 && !optionalAnswer.get().getCreatedBy().equals(operator.getUserId())) {
            throw QuestionException.answerForbidden();
        }
        answerRepository.deleteById(answerId);
    }
}
