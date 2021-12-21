package com.eventeam.events.client;

import com.eventeam.events.client.internal.RequestStatus;
import com.eventeam.events.model.ResponseData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "friend-request")
public interface FriendRequestClient {

    @GetMapping("/friend-request/status/{idUserFrom}/{idUserTo}")
    ResponseEntity<ResponseData<RequestStatus>> findFriendRequestById(@PathVariable String idUserFrom,
                                                                      @PathVariable String idUserTo);
}
