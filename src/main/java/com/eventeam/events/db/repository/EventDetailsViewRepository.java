package com.eventeam.events.db.repository;

import com.eventeam.events.model.dtos.EventDetailsView;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventDetailsViewRepository extends CrudRepository<EventDetailsView, String> {
}
