package com.eventeam.events.client;

import com.eventeam.events.client.internal.UserFriendsView;
import com.eventeam.events.model.ResponseData;
import com.eventeam.events.model.dtos.UserView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient("user-friends")
public interface UserFriendClient {

    @GetMapping("/user-friends/logged/user")
    ResponseEntity<ResponseData<List<UserFriendsView>>> getUserActiveFriends(
            @RequestHeader("X-auth-user-id") String idUser
    );

}
