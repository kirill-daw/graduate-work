package com.skypro.avito.service;

import com.skypro.avito.repository.AdRepository;
import com.skypro.avito.repository.CommentRepository;
import org.springframework.stereotype.Component;

@Component("securityService")
public class SecurityService {

    private final AdRepository adRepository;
    private final CommentRepository commentRepository;

    public SecurityService(AdRepository adRepository, CommentRepository commentRepository) {
        this.adRepository = adRepository;
        this.commentRepository = commentRepository;
    }

    public boolean isAdOwner(Integer adId, String username) {
        return adRepository.findById(adId)
                .map(ad -> ad.getAuthor().getUsername().equals(username))
                .orElse(false);
    }

    public boolean isCommentOwner(Integer commentId, String username) {
        return commentRepository.findById(commentId)
                .map(c -> c.getAuthor().getUsername().equals(username))
                .orElse(false);
    }
}
