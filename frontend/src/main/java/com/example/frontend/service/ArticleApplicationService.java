package com.example.frontend.service;

import com.example.domain.article.model.Article;
import com.example.domain.article.model.ArticleTag;
import com.example.domain.article.model.Tag;
import com.example.domain.article.repository.ArticleRepository;
import com.example.domain.article.repository.TagRepository;
import com.example.domain.article.service.ArticleService;
import com.example.domain.auth.model.Authorize;
import com.example.frontend.usecase.CreateArticleCase;
import com.example.frontend.usecase.GetArticleDetailCase;
import com.example.frontend.usecase.GetArticleTagsCase;
import com.example.frontend.usecase.GetArticlesCase;
import com.example.frontend.usecase.TagArticleCase;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Service
@AllArgsConstructor
public class ArticleApplicationService {
    private final ArticleService service;
    private final ArticleRepository repository;
    private final TagRepository tagRepository;

    public CreateArticleCase.Response create(CreateArticleCase.Request request, Authorize authorize) {
        Article article = service.create(request.getTitle(), request.getContent(), authorize.getUserId());

        for (String tagId : request.getTagIds()) {
            service.addTag(article.getId(), tagId, authorize.getUserId());
        }

        return CreateArticleCase.Response.from(article);
    }

    public GetArticleDetailCase.Response getDetail(String id) {
        Article article = service.get(id);
        return GetArticleDetailCase.Response.from(article);
    }

    public Page<GetArticlesCase.Response> getByPage(Pageable pageable) {
        return repository.findAll(pageable)
                .map(GetArticlesCase.Response::from);
    }

    public TagArticleCase.Response tagArticle(String id, TagArticleCase.Request request, Authorize authorize) {
        ArticleTag articleTag = service.addTag(id, request.getTagId(), authorize.getUserId());
        return TagArticleCase.Response.from(articleTag);
    }

    public List<GetArticleTagsCase.Response> getTags(String id) {
        Article article = service.get(id);
        List<ArticleTag> articleTags = article.getTags();
        Map<String, Tag> tagMap = tagRepository.findAllById(
                articleTags.stream()
                        .map(ArticleTag::getTagId)
                        .collect(Collectors.toList()))
                .stream().collect(toMap(Tag::getId, Function.identity()));

        return articleTags.stream()
                .map(articleTag -> GetArticleTagsCase.Response.from(articleTag, tagMap.get(articleTag.getTagId())))
                .collect(Collectors.toList());
    }
}
