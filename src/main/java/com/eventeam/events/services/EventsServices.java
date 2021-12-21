package com.eventeam.events.services;

import com.eventeam.events.model.ResponseData;
import com.eventeam.events.model.dtos.EventDetailsView;
import com.eventeam.events.model.dtos.EventView;
import com.eventeam.events.model.dtos.EventsSearchView;
import com.eventeam.events.model.enums.EventStatus;
import com.eventeam.events.model.enums.TicketTypeEnum;

import java.util.Date;
import java.util.List;

public interface EventsServices {

    ResponseData<EventDetailsView> create (EventDetailsView events);

    ResponseData<EventDetailsView> update (EventDetailsView events);

    ResponseData<EventDetailsView> get(String idEvents, String idUser);

    ResponseData<EventView> getEventView(String idEvents);

    EventsSearchView getAllEventsWithFiltration(Date startDateTime, TicketTypeEnum ticketType, int pageNumber, int pageSize);

    ResponseData<?> deleteEvent(String id);

    ResponseData<List<EventView>> getPremiumEvents();

    ResponseData<List<EventView>> getUsersEvents(List<String> userIds);

    ResponseData<List<EventView>> getEvents(List<String> eventIds, EventStatus eventStatus);

    ResponseData<List<EventView>> getPassedEvents(String userId);

    ResponseData<List<EventView>> getActiveEvents(String userId);
}
