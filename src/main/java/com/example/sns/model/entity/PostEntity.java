package com.example.sns.model.entity;

import com.example.sns.model.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "\"post\"")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"post\" SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column
    private Timestamp registeredAt;

    @Column
    private Timestamp updatedAt;

    @Column
    private Timestamp deletedAt;

    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    @Builder
    public PostEntity(String title, String body, UserEntity userEntity) {
        this.title = title;
        this.body = body;
        this.user = userEntity;
    }
 }
