package com.eventeam.events.db.repository;

import com.eventeam.events.model.dtos.UserView;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserViewRepository extends CrudRepository<UserView, String> {
}
