package com.skypro.avito.mapper;

import com.skypro.avito.dto.Ad;
import com.skypro.avito.dto.CreateOrUpdateAd;
import com.skypro.avito.dto.ExtendedAd;
import com.skypro.avito.entity.AdEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdMapper {

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.id", target = "author")
    @Mapping(target = "image", expression = "java(\"/images/ads/\" + adEntity.getImage())")
    Ad toAd(AdEntity adEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "comments", ignore = true)
    AdEntity toEntity(CreateOrUpdateAd createOrUpdateAd);

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    @Mapping(source = "author.lastName", target = "authorLastName")
    @Mapping(source = "author.username", target = "email")
    @Mapping(source = "author.phone", target = "phone")
    @Mapping(target = "image", expression = "java(\"/images/ads/\" + adEntity.getImage())")
    ExtendedAd toExtendedAd(AdEntity adEntity);
}
