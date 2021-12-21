package com.eventeam.events.client.internal;


import com.eventeam.events.client.internal.enums.AttendeesType;
import com.eventeam.events.client.internal.enums.EventUserType;
import com.eventeam.events.model.dtos.UserView;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventUserInfo implements Serializable {
    String id;
    UserView user;
    EventUserType eventUserType;
    AttendeesType attendeesType;

}
