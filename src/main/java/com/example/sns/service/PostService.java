package com.example.sns.service;

import com.example.sns.exception.ErrorCode;
import com.example.sns.exception.SnsApplicationException;
import com.example.sns.model.AlarmArgs;
import com.example.sns.model.AlarmType;
import com.example.sns.model.Comment;
import com.example.sns.model.Post;
import com.example.sns.model.entity.*;
import com.example.sns.model.event.AlarmEvent;
import com.example.sns.producer.AlarmProducer;
import com.example.sns.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final LikeEntityRepository likeEntityRepository;
    private final CommentEntityRepository commentEntityRepository;
    private final AlarmProducer alarmProducer;

    @Transactional
    public void create(String title, String body, String username) {
        // user find
        UserEntity userEntity = getUserOrException(username);
        // post save
        postEntityRepository.save(PostEntity.builder()
                .title(title)
                .body(body)
                .userEntity(userEntity)
                .build());

    }

    @Transactional
    public Post modify(String title, String body, String username, Long postId) {
        UserEntity userEntity = getUserOrException(username);
        PostEntity postEntity = getPostOrException(postId);
        // post permission
        if (postEntity.getUser() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, postId));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);
        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }

    @Transactional
    public void delete(String username, Long postId) {
        UserEntity userEntity = getUserOrException(username);
        PostEntity postEntity = getPostOrException(postId);
        // post permission
        if (postEntity.getUser() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, postId));
        }
        likeEntityRepository.deleteAllByPost(postEntity);
        commentEntityRepository.deleteAllByPost(postEntity);
        postEntityRepository.delete(postEntity);
    }

    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(String username, Pageable pageable) {
        UserEntity userEntity = getUserOrException(username);
        return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
    }

    @Transactional
    public void like(String username, Long postId) {
        UserEntity userEntity = getUserOrException(username);
        PostEntity postEntity = getPostOrException(postId);
        //check liked
        likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("username %s already like post %s", username, postId));
        });

        //like save
        likeEntityRepository.save(LikeEntity.builder()
                .userEntity(userEntity)
                .postEntity(postEntity)
                .build());

        alarmProducer.send(AlarmEvent.builder()
                .receiveUserId(postEntity.getUser().getId())
                .alarmType(AlarmType.NEW_LIKE_ON_POST)
                .args(AlarmArgs.builder()
                        .fromUserId(userEntity.getId())
                        .targetId(postEntity.getId())
                        .build())
                .build());
    }

    public long likeCount(Long postId) {
        PostEntity postEntity = getPostOrException(postId);
        //count like
        return likeEntityRepository.countByPost(postEntity);
    }

    @Transactional
    public void comment(Long postId, String comment, String username) {
        UserEntity userEntity = getUserOrException(username);
        PostEntity postEntity = getPostOrException(postId);

        //comment save
        commentEntityRepository.save(CommentEntity.builder()
                .userEntity(userEntity)
                .postEntity(postEntity)
                .comment(comment)
                .build());

        alarmProducer.send(AlarmEvent.builder()
                .receiveUserId(postEntity.getUser().getId())
                .alarmType(AlarmType.NEW_COMMENT_ON_POST)
                .args(AlarmArgs.builder()
                        .fromUserId(userEntity.getId())
                        .targetId(postEntity.getId())
                        .build())
                .build());

    }

    public Page<Comment> getComments(Long postId, Pageable pageable) {
        PostEntity postEntity = getPostOrException(postId);
        return commentEntityRepository.findAllByPost(postEntity, pageable).map(Comment::fromEntity);
    }

    private PostEntity getPostOrException(Long postId) {
        return postEntityRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));
    }

    private UserEntity getUserOrException(String username) {
        return userEntityRepository.findByUsername(username).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username)));
    }
}
