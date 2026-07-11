package com.skypro.avito.service.impl;

import com.skypro.avito.dto.Comment;
import com.skypro.avito.dto.Comments;
import com.skypro.avito.dto.CreateOrUpdateComment;
import com.skypro.avito.entity.AdEntity;
import com.skypro.avito.entity.CommentEntity;
import com.skypro.avito.entity.UserEntity;
import com.skypro.avito.exception.AdNotFoundException;
import com.skypro.avito.exception.CommentNotFoundException;
import com.skypro.avito.exception.UserNotFoundException;
import com.skypro.avito.mapper.CommentMapper;
import com.skypro.avito.repository.AdRepository;
import com.skypro.avito.repository.CommentRepository;
import com.skypro.avito.repository.UserRepository;
import com.skypro.avito.service.CommentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentRepository commentRepository,
                              AdRepository adRepository,
                              UserRepository userRepository,
                              CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.adRepository = adRepository;
        this.userRepository = userRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public Comments getComments(Integer adId) {
        List<CommentEntity> commentEntities = commentRepository.findByAdId(adId);
        List<Comment> comments = commentEntities.stream()
                .map(commentMapper::toComment)
                .collect(Collectors.toList());
        return new Comments(comments.size(), comments);
    }

    @Override
    public Comment addComment(Integer adId, CreateOrUpdateComment createOrUpdateComment, String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        AdEntity ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException(adId));
        CommentEntity commentEntity = commentMapper.toEntity(createOrUpdateComment);
        commentEntity.setAuthor(user);
        commentEntity.setAd(ad);
        commentEntity.setCreatedAt(System.currentTimeMillis());
        CommentEntity saved = commentRepository.save(commentEntity);
        return commentMapper.toComment(saved);
    }

    @Override
    @PreAuthorize("@securityService.isCommentOwner(#commentId, authentication.name) || hasRole('ADMIN')")
    public void deleteComment(Integer adId, Integer commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    @PreAuthorize("@securityService.isCommentOwner(#commentId, authentication.name) || hasRole('ADMIN')")
    public Comment updateComment(Integer adId, Integer commentId, CreateOrUpdateComment createOrUpdateComment) {
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        commentEntity.setText(createOrUpdateComment.getText());
        CommentEntity saved = commentRepository.save(commentEntity);
        return commentMapper.toComment(saved);
    }
}
