package com.eventeam.events.db.repository;

import com.eventeam.events.model.dtos.HostPassedEvents;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HostPassedEventsRepository extends CrudRepository<HostPassedEvents, String> {
}
