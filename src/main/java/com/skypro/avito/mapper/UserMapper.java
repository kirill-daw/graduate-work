package com.skypro.avito.mapper;

import com.skypro.avito.dto.RegisterReq;
import com.skypro.avito.dto.UpdateUser;
import com.skypro.avito.dto.User;
import com.skypro.avito.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "ads", ignore = true)
    @Mapping(target = "comments", ignore = true)
    UserEntity toEntity(RegisterReq registerReq);

    User toUser(UserEntity userEntity);

    default String mapRoleToString(com.skypro.avito.dto.Role role) {
        return role != null ? role.name() : null;
    }

    UpdateUser toUpdateUser(UserEntity userEntity);
}
