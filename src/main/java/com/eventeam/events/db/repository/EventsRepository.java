package com.eventeam.events.db.repository;

import com.eventeam.events.db.entity.Events;
import com.eventeam.events.model.enums.EventStatus;
import com.eventeam.events.model.enums.PrivacyType;
import com.eventeam.events.model.enums.ProcessEvents;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public interface EventsRepository extends CrudRepository<Events , String> {

    List<Events> findAllBy();

   // List<Events> findByIdEvents(String idEvents);

    List<Events> findAllByIdUserInAndProcessStatus(List<String> idUsers, EventStatus eventStatus);

    List<Events> findAllByIdUserAndProcessStatus(String idUser, EventStatus eventStatus);

    List<Events> findAllByIdEventsInAndProcessStatus(List<String> idEvents, EventStatus eventStatus);

    List<Events> getAllByEventType(PrivacyType eventType);

//    List<Events> findByIdEventsAndStartDateTime(String idEvents, Date startDateTime);

    List<Events> findAllByPremiumOrderGreaterThanAndProcessStatus(int premiumOrder, EventStatus eventStatus);

    List<Events> findAllByPremiumOrderEqualsAndProcessStatusAndEndDateEqualsAndEndTimeLessThanEqual(int premiumOrder,
                                                                                                    EventStatus eventStatus,
                                                                                                    LocalDate endDate,
                                                                                                    LocalTime endTime
                                                                                                    );

    List<Events> findAllByCachedAndProcessStatus(int cached, EventStatus eventStatus);

}
