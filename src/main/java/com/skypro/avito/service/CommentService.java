package com.skypro.avito.service;

import com.skypro.avito.dto.Comment;
import com.skypro.avito.dto.Comments;
import com.skypro.avito.dto.CreateOrUpdateComment;

public interface CommentService {

    Comments getComments(Integer adId);

    Comment addComment(Integer adId, CreateOrUpdateComment createOrUpdateComment, String username);

    void deleteComment(Integer adId, Integer commentId);

    Comment updateComment(Integer adId, Integer commentId, CreateOrUpdateComment createOrUpdateComment);
}
