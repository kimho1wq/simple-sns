package com.example.sns.model.event;

import com.example.sns.model.AlarmArgs;
import com.example.sns.model.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class AlarmEvent {
    private Long receiveUserId;
    private AlarmType alarmType;
    private AlarmArgs args;

    @Builder
    public AlarmEvent(Long receiveUserId, AlarmType alarmType, AlarmArgs args) {
        this.receiveUserId = receiveUserId;
        this.alarmType = alarmType;
        this.args = args;
    }
}
