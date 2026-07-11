package com.skypro.avito.mapper;

import com.skypro.avito.dto.Comment;
import com.skypro.avito.dto.CreateOrUpdateComment;
import com.skypro.avito.entity.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "author.id", target = "author")
    Comment toComment(CommentEntity commentEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "ad", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    CommentEntity toEntity(CreateOrUpdateComment createOrUpdateComment);
}
