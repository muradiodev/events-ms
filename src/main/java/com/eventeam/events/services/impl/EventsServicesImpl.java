package com.eventeam.events.services.impl;

import com.eventeam.events.client.*;
import com.eventeam.events.client.internal.*;
import com.eventeam.events.client.internal.enums.AttendeesType;
import com.eventeam.events.client.internal.enums.EventUserType;
import com.eventeam.events.client.internal.enums.NotificationType;
import com.eventeam.events.db.entity.Events;
import com.eventeam.events.db.repository.*;
import com.eventeam.events.kafka.EventKafka;
import com.eventeam.events.mapper.EventsMapper;
import com.eventeam.events.model.ResponseData;
import com.eventeam.events.model.dtos.*;
import com.eventeam.events.model.enums.EventStatus;
import com.eventeam.events.model.enums.TicketTypeEnum;
import com.eventeam.events.exceptions.NotFoundException;
import com.eventeam.events.services.EventsServices;
import com.eventeam.events.util.generateresponse.GenerateResponseUtility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@Service
public class EventsServicesImpl implements EventsServices {

    private static String SUCCESS_MESSAGE = "SUCCESS";
    private static String NOT_FOUND_MESSAGE = "NOT FOUND";
    private static int SUCCESS_CODE = 200;
    private static int NOT_FOUND_CODE = 404;
    private static final int CLIENT_SERVICE_CODE = 200;
    private final EventsRepository eventsRepository;
    private final EntityManager em;
    private final EventsMapper eventsMapper = EventsMapper.INSTANCE;
    private final CategoryClient categoryClient;
    private final PhotosClient photosClient;
    private final EventUsersClient eventUsersClient;
    private final UsersClient usersClient;
    private final UserViewRepository userViewRepository;
    private final EventViewRepository eventViewRepository;
    private final EventDetailsViewRepository eventDetailsViewRepository;
    private final HostActiveEventsRepository hostActiveEventsRepository;
    private final HostPassedEventsRepository hostPassedEventsRepository;
    private final FriendRequestClient friendRequestClient;
    private final UserFriendClient userFriendClient;
    private final EventKafka eventKafka;


    @Override
    public ResponseData<EventDetailsView> create(EventDetailsView eventDetailsView) {
        Events events = eventsMapper.mapToEvent(eventDetailsView);
        events.setProcessStatus(EventStatus.PUBLISHED);
        events = eventsRepository.save(events);

        EventCategories category = new EventCategories();
        category.setEventId(events.getIdEvents());
        category.setCategories(eventDetailsView.getListOfCategories());
        ResponseEntity<ResponseData<String>> response = categoryClient.create(category);
        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("event category saved");
        }


        if (eventDetailsView.getAllPhoto() != null && !eventDetailsView.getAllPhoto().isEmpty()) {
            for (Photo photo : eventDetailsView.getAllPhoto()) {
                photo.setEventId(events.getIdEvents());
                savePhoto(photo);
            }
        }

        if (eventDetailsView.getCoverPhoto() != null) {
            eventDetailsView.getCoverPhoto().setEventId(events.getIdEvents());
            savePhoto(eventDetailsView.getCoverPhoto());
            events.setIdCoverPhoto(eventDetailsView.getCoverPhoto().getIdPhoto());
        }


        events = eventsRepository.save(events);
        eventDetailsView.setIdEvent(events.getIdEvents());
        createEventUserViewForCoHost(events.getIdEvents(), events.getIdUser());
        List<UserFriendsView> friends = getFriends(events.getIdUser());
        sendCreatedEventNotification(friends != null ? friends.stream().map(UserFriendsView::getFriend).collect(Collectors.toList()) : null,
                NotificationType.FRIENDS_CREATED_EVENTS
                );
        return GenerateResponseUtility.eventDetailFunc.generate(SUCCESS_CODE, SUCCESS_MESSAGE, eventDetailsView);

    }


    private void savePhoto(Photo photo) {
        ResponseEntity<ResponseData<String>> response = photosClient.create(photo);
        if (response.getStatusCode() == HttpStatus.OK) {
            photo.setIdPhoto(response.getBody().getBody());
        }
    }


    @Override
    public ResponseData<EventDetailsView> update(EventDetailsView events) {
        return create(events);
    }

    @Override
    public ResponseData<EventDetailsView> get(String idEvents, String idUser) {
//        EventDetailsView eventDetailsView = redisService.get(idEvents, RedisMapKey.MAP_OF_EVENT_DETAIL_VIEW);
        Optional<EventDetailsView> eventDetailsView = eventDetailsViewRepository.findById(idEvents);
        if (eventDetailsView.isPresent()) {
            log.info("event detail view found from redis : {}", idEvents);
            eventDetailsView.get().setRequestStatus(getUsersFriendsStatus(idUser, eventDetailsView.get().getUserView().getId()));
            return GenerateResponseUtility.eventDetailFunc.generate(SUCCESS_CODE, SUCCESS_MESSAGE, eventDetailsView.get());
        }

        return eventsRepository.findById(idEvents).map(event -> ResponseData.<EventDetailsView>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .body(createEventDetailsView(event, idUser))
                .build()).orElse(ResponseData.<EventDetailsView>builder()
                .code(NOT_FOUND_CODE)
                .message(NOT_FOUND_MESSAGE)
                .build());
    }


    private EventDetailsView createEventDetailsView(Events event, String idUser) {
        EventDetailsView eventDetailsView = eventsMapper.mapToDetailsView(event);
        eventDetailsView.setEventUsersView(getEventUsersView(event.getIdEvents()));
        eventDetailsView.setUserView(getUserView(event.getIdUser()));
        eventDetailsView.setCoverPhoto(getCoverPhoto(event.getIdCoverPhoto()));
        eventDetailsView.setAllPhoto(getEventAllPhotos(event.getIdEvents()));
        eventDetailsView.setListOfCategories(getCategories(event.getIdEvents()));
        return eventDetailsView;
    }

    private List<Property> getCategories(String idEvents) {

        try {
            ResponseEntity<ResponseData<EventCategories>> response = categoryClient.getAllActiveByEventId(idEvents);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody().getCode() == CLIENT_SERVICE_CODE) {
                return response.getBody().getBody().getCategories();
            }

            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Error get Categories: ", e);
            return null;
        }
    }

    private void createEventUserViewForCoHost(String eventId, String userId) {
        log.info("Send request EventUserView");
        eventUsersClient.create(EventUsers.builder()
                .idUser(userId)
                .idEvent(eventId)
                .attendeesType(AttendeesType.GOING)
                .eventUserType(EventUserType.COHOST)
                .build());
    }

    private List<UserFriendsView> getFriends(String idUser) {
        log.info("Get all friends for user : {}", idUser);
        ResponseEntity<ResponseData<List<UserFriendsView>>> response = userFriendClient.getUserActiveFriends(idUser);
        if (
                response.getStatusCode() == HttpStatus.OK
                        && response.getBody() != null
                        && response.getBody().getCode() == CLIENT_SERVICE_CODE
        ) {
            log.info("user friends : {}", response.getBody().getBody());
            return response.getBody().getBody();
        }
        log.info("users friends not found");
        return null;
    }

    private void sendCreatedEventNotification(List<UserView> friends, NotificationType topic) {
        log.info("Send Event Notification");
        if (friends != null) {
            for (UserView friend : friends) {
                eventKafka.sendData(
                        NotficationDto.builder()
                                .userId(friend.getId())
                                .userName(friend.getUserName())
                                .topic(topic)
                                .token(friend.getFcmToken())
                                .build());
            }
        }

    }


    @Override
    public ResponseData<EventView> getEventView(String idEvents) {
        EventView eventView = getFromRedisOrCreat(idEvents);
        return GenerateResponseUtility.eventFunc.generate(
                eventView == null ? NOT_FOUND_CODE : SUCCESS_CODE,
                eventView == null ? NOT_FOUND_MESSAGE : SUCCESS_MESSAGE,
                eventView
        );
    }

    private RequestStatus getUsersFriendsStatus(String idUserFrom, String idToUser) {
        log.info("getting userFrom : {}, user to : {} friends request status ... ", idUserFrom, idToUser);
        ResponseEntity<ResponseData<RequestStatus>> response = friendRequestClient.findFriendRequestById(idUserFrom, idToUser);
        if (response.getStatusCode() == HttpStatus.OK
                && response.getBody() != null
                && response.getBody().getCode() == SUCCESS_CODE) {
            return response.getBody().getBody();
        }

        return null;
    }

    private EventView getFromRedisOrCreat(String idEvent) {
//        EventView eventView = redisService.get(idEvent, RedisMapKey.MAP_OF_EVENT_VIEW);
        Optional<EventView> eventView = eventViewRepository.findById(idEvent);
        return eventView.orElseGet(() -> eventsRepository.findById(idEvent).map(this::createEventView).orElseGet(() -> null));
    }

    /**
     * EventView creat edirik
     *
     * @param event
     * @return EventView
     */
    private EventView createEventView(Events event) {
        EventView eventView = eventsMapper.mapToEventView(event);
        eventView.setCoverPhoto(getCoverPhoto(event.getIdCoverPhoto()));
        eventView.setEventUsersView(getEventUsersView(event.getIdEvents()));
        return eventView;
    }

    private List<Photo> getEventAllPhotos(String idEvents) {
        ResponseEntity<ResponseData<List<Photo>>> response = photosClient.getAll(idEvents);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody().getCode() == CLIENT_SERVICE_CODE) {
            return response.getBody().getBody();
        }

        return new ArrayList<>();
    }

    private Photo getCoverPhoto(String idCoverPhoto) {
        ResponseEntity<ResponseData<Photo>> response = photosClient.get(idCoverPhoto);
        log.debug("idCoverPhoto: {}", idCoverPhoto);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody().getCode() == CLIENT_SERVICE_CODE) {
            return response.getBody().getBody();
        }
        return null;
    }


    private UserView getUserView(String idUser) {
        ResponseEntity<ResponseData<UserView>> response = usersClient.get(idUser);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody().getCode() == CLIENT_SERVICE_CODE) {
            return response.getBody().getBody();
        }
        return null;
    }


    private EventUsersView getEventUsersView(String idEvent) {
        ResponseEntity<ResponseData<EventUsersView>> response = eventUsersClient.getAllEventUsers(idEvent);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody().getCode() == CLIENT_SERVICE_CODE) {
            return response.getBody().getBody();
        }
        return null;
    }


    private Optional<TypedQuery<Events>> findByEventNameOrEventDateTimeOrAmount(Date startDateTime,
                                                                                TicketTypeEnum ticketType) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Events> cq = cb.createQuery(Events.class);
        Root<Events> root = cq.from(Events.class);
        cq.select(root);

        List<Predicate> predicateList = new ArrayList<>();

        if (startDateTime != null)
            predicateList.add(cb.like(root.get("startDateTime"), "%" + startDateTime + "%"));

        if (ticketType != null)
            predicateList.add(cb.like(root.get("ticketType"), "%" + ticketType + "%"));

        predicateList.add(cb.like(root.get("processStatus"), "%" + EventStatus.PUBLISHED + "%"));

        cq.where(predicateList.toArray(new Predicate[predicateList.size()]));

        return Optional.of(em.createQuery(cq));

    }

    @Override
    public EventsSearchView getAllEventsWithFiltration(Date startDateTime, TicketTypeEnum ticketType,
                                                       int pageNumber, int pageSize) {
        return findByEventNameOrEventDateTimeOrAmount(startDateTime, ticketType)
                .map(typedQuery -> createEventsDto(typedQuery, pageNumber, pageSize))
                .orElseThrow(() -> new NotFoundException("Events not found"));
    }


    public EventsSearchView createEventsDto(TypedQuery<Events> typedQuery, int pageNumber, int pageSize) {
        return EventsSearchView.builder()
                .eventListSize(typedQuery.getResultList().size())
                .eventsList(createEventSearchViewList(getEventsByPageNumberAndPageSize(typedQuery, pageNumber, pageSize)))
                .build();
    }

    private List<EventView> createEventSearchViewList(List<Events> list) {
        return null;
    }


    private List<Events> getEventsByPageNumberAndPageSize(TypedQuery<Events> typedQuery, int pageNumber, int pageSize) {
        List<Events> resultList = typedQuery.setFirstResult(pageNumber * pageSize).setMaxResults(pageSize).getResultList();
        if (resultList.isEmpty()) throw new NotFoundException("Events not Found");
        return resultList;
    }


    @Override
    public ResponseData<?> deleteEvent(String id) {
        Optional<Events> event = eventsRepository.findById(id);
        if (event.isPresent()) {
            event.get().setProcessStatus(EventStatus.DELETED);
            return GenerateResponseUtility.eventFunc.generate(SUCCESS_CODE, SUCCESS_MESSAGE, null);
        } else {
            return GenerateResponseUtility.eventFunc.generate(NOT_FOUND_CODE, NOT_FOUND_MESSAGE, null);
        }

    }


    @Override
    public ResponseData<List<EventView>> getPremiumEvents() {

        List<Events> list = eventsRepository.findAllByPremiumOrderGreaterThanAndProcessStatus(0, EventStatus.PUBLISHED);
        log.info("Premium Events: {}", list);
        return createListResponse(list);
    }

    @Override
    public ResponseData<List<EventView>> getUsersEvents(List<String> userIds) {
        log.info("Getting users events : {}", userIds);
        List<Events> list = eventsRepository.findAllByIdUserInAndProcessStatus(userIds, EventStatus.PUBLISHED);
        log.info("users events : {}", list);
        return createListResponse(list);
    }

    @Override
    public ResponseData<List<EventView>> getEvents(List<String> eventIds, EventStatus eventStatus) {
        log.info("starting to get events for ids : {}", eventIds);
        List<Events> list = eventsRepository.findAllByIdEventsInAndProcessStatus(eventIds, eventStatus);
        log.info("events : {}", list);
        if (list == null || list.isEmpty()) {
            log.info("events not found");
            return GenerateResponseUtility.eventListFunc.generate(NOT_FOUND_CODE, NOT_FOUND_MESSAGE, null);
        }
        List<EventView> collect = list.stream()
                .map(this::getEventView)
                .collect(Collectors.toList());
        return GenerateResponseUtility.eventListFunc.generate(SUCCESS_CODE, SUCCESS_MESSAGE, collect);
    }

    /**
     * Event veririk varsa redisden EventView gotururuk
     * yoxdursa creat edirik
     *
     * @param event
     * @return
     */
    private EventView getEventView(Events event) {
//        EventView eventView = redisService.get(event.getIdEvents(), RedisMapKey.MAP_OF_EVENT_VIEW);
//        Optional<EventView> eventView = eventViewRepository.findById(event.getIdEvents());
        return eventViewRepository.findById(event.getIdEvents()).orElseGet(() -> createEventView(event));
    }


    @Override
    public ResponseData<List<EventView>> getActiveEvents(String userId) {
        log.info("starting to get user active events : {}", userId);
        Optional<HostActiveEvents> active = hostActiveEventsRepository.findById(userId);
        if (active.isPresent() && !active.get().getHostActiveEventsIds().isEmpty()) {
            log.info("user active events not empty in redis server.....");
            return GenerateResponseUtility.eventListFunc.generate(
                    SUCCESS_CODE,
                    SUCCESS_MESSAGE,
                    active.get().getHostActiveEventsIds().stream()
                            .map(this::getFromRedisOrCreat)
                            .collect(Collectors.toList())
            );
        } else {
            log.info("active events not found in redis server for userId : {}", userId);
            List<Events> listEvents = eventsRepository.findAllByIdUserAndProcessStatus(userId, EventStatus.PUBLISHED);
            log.info("lsit events from db : {}", listEvents);
            return GenerateResponseUtility.eventListFunc.generate(
                    listEvents == null || listEvents.isEmpty() ? NOT_FOUND_CODE : SUCCESS_CODE,
                    listEvents == null || listEvents.isEmpty() ? NOT_FOUND_MESSAGE : SUCCESS_MESSAGE,
                    listEvents == null || listEvents.isEmpty() ? null : listEvents.stream()
                            .map(this::createEventView)
                            .collect(Collectors.toList())
            );
        }
    }


    @Override
    public ResponseData<List<EventView>> getPassedEvents(String userId) {
//        List<String> listFromRedis = redisService.get(userId, RedisMapKey.MAP_OF_PAST_EVENTS);
        Optional<HostPassedEvents> passed = hostPassedEventsRepository.findById(userId);

        if (passed.isPresent() && !passed.get().getHostPassedEventsIds().isEmpty()) {
            return GenerateResponseUtility.eventListFunc.generate(
                    SUCCESS_CODE,
                    SUCCESS_MESSAGE,
//                    listFromRedis.stream()
                    passed.get().getHostPassedEventsIds().stream()
                            .map(this::getFromRedisOrCreat)
                            .collect(Collectors.toList())
            );
        } else {

            List<Events> listEvents = eventsRepository.findAllByIdUserAndProcessStatus(userId, EventStatus.PASSED);
            return GenerateResponseUtility.eventListFunc.generate(
                    listEvents == null || listEvents.isEmpty() ? NOT_FOUND_CODE : SUCCESS_CODE,
                    listEvents == null || listEvents.isEmpty() ? NOT_FOUND_MESSAGE : SUCCESS_MESSAGE,
                    listEvents == null || listEvents.isEmpty() ? null : listEvents.stream()
                            .map(this::createEventView)
                            .collect(Collectors.toList())
            );

        }
    }

    ResponseData<List<EventView>> createListResponse(List<Events> list) {
        if (!list.isEmpty()) {
            return ResponseData.<List<EventView>>builder()
                    .code(SUCCESS_CODE)
                    .message(SUCCESS_MESSAGE)
                    .body(list.stream()
                            .map(this::getEventView)
                            .collect(Collectors.toList()))
                    .build();
        } else {
            return GenerateResponseUtility.eventListFunc.generate(NOT_FOUND_CODE, NOT_FOUND_MESSAGE, null);
        }
    }

    private void startAddingEventToRedis(EventDetailsView eventDetailsView, EventView eventView) {
        addEventDetailsViewToRedisMap(eventDetailsView);
        addEventViewToRedisMap(eventView);
        addEventViewToHostActiveRedisList(eventDetailsView.getUserView().getId(), eventView.getIdEvent());
//        addEventViewToUserRedisListMap(eventDetailsView.getUserView().getId(), eventView.getIdEvent(), RedisMapKey.MAP_OF_HOST_ACTIVE_EVENTS);
    }

    private void addEventViewToRedisMap(EventView eventView) {
        log.debug("trying to save eventView to redis {}", eventView);
        eventViewRepository.save(eventView);
    }

    private void addEventDetailsViewToRedisMap(EventDetailsView eventDetailsView) {
        eventDetailsViewRepository.save(eventDetailsView);
    }


    private void addEventViewToUserPassedListMap(String idUser, String idEvent) {
        removeEventViewFromUserActiveListMap(idUser, idEvent);
        addEventViewToHostPassedRedisList(idUser, idEvent);

    }

    private void addEventViewToHostPassedRedisList(String idUser, String idEvent) {
        Optional<HostPassedEvents> hostPassedEvents = hostPassedEventsRepository.findById(idUser);
        if (hostPassedEvents.isPresent()) {
            hostPassedEvents.get().getHostPassedEventsIds().add(idEvent);
            hostPassedEventsRepository.save(hostPassedEvents.get());
        } else {
            log.info("host active events list not found in redis server.....");
            HostPassedEvents build = HostPassedEvents.builder()
                    .id(idUser)
                    .hostPassedEventsIds(Collections.singletonList(idEvent))
                    .build();
            hostPassedEventsRepository.save(build);
        }
    }

    private void addEventViewToHostActiveRedisList(String idUser, String idEvent) {
        Optional<HostActiveEvents> hostActiveEvents = hostActiveEventsRepository.findById(idUser);
        if (hostActiveEvents.isPresent()) {
            hostActiveEvents.get().getHostActiveEventsIds().add(idEvent);
            hostActiveEventsRepository.save(hostActiveEvents.get());
        } else {
            log.info("host active events list not found in redis server.....");
            HostActiveEvents build = HostActiveEvents.builder()
                    .id(idUser)
                    .hostActiveEventsIds(Collections.singletonList(idEvent))
                    .build();
            hostActiveEventsRepository.save(build);
        }
    }


    private void removeEventViewFromUserActiveListMap(String idUser, String idEvent) {
//        Map<String, List<String>> map = redisService.get(RedisMapKey.MAP_OF_HOST_ACTIVE_EVENTS);
        Optional<HostActiveEvents> byId = hostActiveEventsRepository.findById(idUser);
        if (byId.isPresent() && !byId.get().getHostActiveEventsIds().isEmpty()) {
//            List<String> list = redisService.get(idUser, RedisMapKey.MAP_OF_HOST_ACTIVE_EVENTS);

            log.info("Users:{} active events: {} ", idUser, byId.get().getHostActiveEventsIds());
            byId.get().getHostActiveEventsIds().remove(idEvent);
            hostActiveEventsRepository.save(byId.get());
        }

    }

    @Scheduled(fixedDelay = 1000 * 60)
    private void queueTask() {
        log.info("starting cache");
        List<Events> list = eventsRepository.findAllByCachedAndProcessStatus(0, EventStatus.PUBLISHED);
        log.info("listOfEventSize: {}", list.size());
        list.stream()
                .forEach(event -> {
                    startAddingEventToRedis(createEventDetailsView(event, ""), createEventView(event));
                    event.setCached(1);
                    eventsRepository.save(event);
                });
    }


    @Scheduled(fixedDelay = 1000 * 60)
    private void checkForPassedEvents() {
        log.info("*************************************************************************\n Scheduled started");
        List<Events> list = eventsRepository.findAllByPremiumOrderEqualsAndProcessStatusAndEndDateEqualsAndEndTimeLessThanEqual(0, EventStatus.PUBLISHED,
                LocalDate.now(), LocalTime.now());
        log.info("Events: {}", list);
        for (Events event : list) {
            log.info("Trying to add passed list : {}", event);
            event.setProcessStatus(EventStatus.PASSED);
            eventsRepository.save(event);
            addEventViewToUserPassedListMap(event.getIdUser(), event.getIdEvents());
        }
    }


}
