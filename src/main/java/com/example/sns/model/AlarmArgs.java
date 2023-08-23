package com.example.sns.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class AlarmArgs {

    private Long fromUserId; // 알람을 발생시킨 사람
    private Long targetId; // post, comment 등의 id

    @Builder
    public AlarmArgs(Long fromUserId, Long targetId) {
        this.fromUserId = fromUserId;
        this.targetId = targetId;
    }
}
// comment: 00씨가 새 코멘트를 작성했습니다 -> postid, commentid