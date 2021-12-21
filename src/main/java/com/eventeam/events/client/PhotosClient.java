package com.eventeam.events.client;


import com.eventeam.events.client.internal.Photo;
import com.eventeam.events.model.ResponseData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "photos-ms")
public interface PhotosClient {


    @GetMapping("/photos/{id}")
    ResponseEntity<ResponseData<Photo>> get(@PathVariable String id);

    @GetMapping("/photos/user/{userId}")
    ResponseEntity<ResponseData<List<Photo>>> getAll(@PathVariable String userId);

    @PostMapping("/photos")
    ResponseEntity<ResponseData<String>> create(@RequestBody Photo photo);

}
