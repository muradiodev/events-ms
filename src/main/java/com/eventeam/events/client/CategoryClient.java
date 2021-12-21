package com.eventeam.events.client;

import com.eventeam.events.client.internal.EventCategories;
import com.eventeam.events.model.ResponseData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "eventcategory")
public interface CategoryClient {

    @PostMapping("/eventcategory")
    ResponseEntity<ResponseData<String>> create(@RequestBody EventCategories eventCategories);

    @GetMapping("/eventcategory/event/{eventId}")
    ResponseEntity<ResponseData<EventCategories>> getAllActiveByEventId(@PathVariable String eventId);


    @PutMapping("/eventcategory/{eventId}")
    ResponseData<String> update(@PathVariable String eventId);

}


