package com.example.sns.fixture;


import com.example.sns.model.entity.PostEntity;
import com.example.sns.model.entity.UserEntity;

public class PostEntityFixture {
    public static PostEntity get(String username, Long postId, Long userId) {
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername(username);

        PostEntity result = new PostEntity();
        result.setUser(user);
        result.setId(postId);

        return result;
    }
}
