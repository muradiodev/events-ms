package com.eventeam.events.model.dtos;

import com.eventeam.events.db.entity.Events;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventsSearchView {

    List<EventView> eventsList;
    int eventListSize;
}
