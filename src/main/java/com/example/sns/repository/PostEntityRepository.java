package com.example.sns.repository;

import com.example.sns.model.entity.PostEntity;
import com.example.sns.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Long> {
    Page<PostEntity> findAllByUser(UserEntity entity, Pageable pageable);
}