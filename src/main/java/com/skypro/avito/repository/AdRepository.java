package com.skypro.avito.repository;

import com.skypro.avito.entity.AdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdRepository extends JpaRepository<AdEntity, Integer> {

    List<AdEntity> findAllByAuthorId(Integer authorId);
}
