package com.eventeam.events.client;

import com.eventeam.events.client.internal.EventUsers;
import com.eventeam.events.client.internal.EventUsersView;
import com.eventeam.events.client.internal.enums.AttendeesType;
import com.eventeam.events.model.ResponseData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient("eventusers")
public interface EventUsersClient {

    @DeleteMapping("/api/v1/eventUsers/idEvent/{idEvent}")
    ResponseEntity<?> deleteByEventId(@PathVariable String idEvent);

    @GetMapping("/api/v1/eventUsers/countAll/idEvent/{idEvent}")
    ResponseEntity<?> countAll(@PathVariable String idEvent);

    @GetMapping("/api/v1/eventUsers/idEvent/{idEvent}")
    ResponseEntity<ResponseData<EventUsersView>> getAllEventUsers(@PathVariable String idEvent);

    @GetMapping("/api/v1/eventUsers/countAll/idEvent/attendeesType/{idEvent}/{attendeesType}")
    ResponseEntity<?> countAllUsers(@PathVariable String idEvent, @PathVariable AttendeesType attendeesType);


    @PostMapping("/api/v1/eventUsers/eventUser")
    ResponseEntity<ResponseData<EventUsers>> create(@RequestBody EventUsers eventUser);


}
