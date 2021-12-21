package com.eventeam.events.client.internal;

import com.eventeam.events.model.dtos.UserView;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFriendsView {
    String id;
    UserView friend;
    String status;
}
