package com.eventeam.events.api;

import com.eventeam.events.db.entity.Events;
import com.eventeam.events.model.ResponseData;
import com.eventeam.events.model.dtos.EventDetailsView;
import com.eventeam.events.model.dtos.EventView;
import com.eventeam.events.model.dtos.EventsSearchView;
import com.eventeam.events.model.dtos.UserView;
import com.eventeam.events.model.enums.EventStatus;
import com.eventeam.events.model.enums.TicketTypeEnum;
import com.eventeam.events.services.EventsServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/events")
public class EventsController {


    private final EventsServices eventsServices;


    @GetMapping("/{idEvents}")
    public ResponseEntity<ResponseData<EventDetailsView>> getEvent(
            @RequestHeader("X-auth-user-id") String idUser,
            @PathVariable @Validated String idEvents
    ){
        return ResponseEntity.ok(eventsServices.get(idEvents, idUser));
    }

    @GetMapping("/simple/{idEvent}")
    public ResponseEntity<ResponseData<EventView>> getEventView(@PathVariable @Validated String idEvent){
        return ResponseEntity.ok(eventsServices.getEventView(idEvent));
    }


    @PostMapping
    public ResponseEntity<ResponseData<EventDetailsView>> create(
            @RequestHeader("X-auth-user-id") String userId,
            @RequestBody @Valid EventDetailsView events){
        events.setUserView(UserView.builder().id(userId).build());
        return ResponseEntity.ok(eventsServices.create(events));
    }

    @PutMapping
    public ResponseEntity<ResponseData<EventDetailsView>> update(@RequestBody @Valid EventDetailsView events){

        return ResponseEntity.ok(eventsServices.update(events));
    }


    @GetMapping("/filter")
    public EventsSearchView GetAllWithFiltration(
                                          @RequestParam(required = false) Date eventDateTime,
                                          @RequestParam(required = false) TicketTypeEnum ticketType,
                                          @RequestParam(required = false, defaultValue = "0") int pageNumber,
                                          @RequestParam(required = false, defaultValue = "10") int pageSize ){
        return eventsServices.getAllEventsWithFiltration( eventDateTime, ticketType, pageNumber, pageSize);
    }


    @GetMapping("/premium")
    public ResponseEntity<ResponseData<List<EventView>>> getPremiumEvents(){
        return ResponseEntity.ok(eventsServices.getPremiumEvents());
    }

    @GetMapping("/list-user/{userIds}")
    public ResponseEntity<ResponseData<List<EventView>>> getUsersEvents(@PathVariable List<String> userIds){
        return ResponseEntity.ok(eventsServices.getUsersEvents(userIds));
    }

    @GetMapping("/list-ids/{eventIds}")
    public ResponseEntity<ResponseData<List<EventView>>> getEvents(@PathVariable List<String> eventIds){
        return ResponseEntity.ok(eventsServices.getEvents(eventIds, EventStatus.PUBLISHED));
    }


    @GetMapping("/passed-events/{eventIds}")
    public ResponseEntity<ResponseData<List<EventView>>> getUserPassedEvents(@PathVariable List<String> eventIds){
        return ResponseEntity.ok(eventsServices.getEvents(eventIds, EventStatus.PASSED));
    }

    @GetMapping("/user/passed-events/{userId}")
    public ResponseEntity<ResponseData<List<EventView>>> getHostPassedEvents(@PathVariable String userId){
        return ResponseEntity.ok(eventsServices.getPassedEvents(userId));
    }

    @GetMapping("/user/active-events/{userId}")
    public ResponseEntity<ResponseData<List<EventView>>> getHostActiveEvents(@PathVariable String userId){
        return ResponseEntity.ok(eventsServices.getActiveEvents(userId));
    }

    @PutMapping("/delete/{eventId}")
    public ResponseEntity<ResponseData<?>> delete(@PathVariable String eventId){
        return ResponseEntity.ok(eventsServices.deleteEvent(eventId));
    }
}
