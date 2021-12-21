package com.eventeam.events.model.dtos;

import com.eventeam.events.client.internal.Photo;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("map_of_users_view")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserView implements Serializable {

    @Id
    String id;
    String userName;
    String email;
    String birthday;
    String gender;
    Photo profilePhoto;
    int isActive;
    String fcmToken;
}
