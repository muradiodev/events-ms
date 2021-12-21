package com.eventeam.events.db.repository;

import com.eventeam.events.model.dtos.EventView;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventViewRepository extends CrudRepository<EventView, String> {
}
