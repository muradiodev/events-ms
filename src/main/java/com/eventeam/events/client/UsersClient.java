package com.eventeam.events.client;

import com.eventeam.events.model.ResponseData;
import com.eventeam.events.model.dtos.UserView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user")
public interface UsersClient {

    @GetMapping("/user/{id}")
    ResponseEntity<ResponseData<UserView>> get(@PathVariable String id);

    }
