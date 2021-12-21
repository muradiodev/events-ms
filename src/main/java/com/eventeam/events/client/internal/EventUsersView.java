package com.eventeam.events.client.internal;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventUsersView implements Serializable {

    int goingCount;
    int interestedCount;
    int expectedCount;
    List<EventUserInfo> goingList;
    List<EventUserInfo> interestedList;
    List<EventUserInfo> expectedList;

}
