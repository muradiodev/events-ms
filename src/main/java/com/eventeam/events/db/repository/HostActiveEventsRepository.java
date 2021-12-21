package com.eventeam.events.db.repository;

import com.eventeam.events.model.dtos.HostActiveEvents;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HostActiveEventsRepository extends CrudRepository<HostActiveEvents, String> {
}
