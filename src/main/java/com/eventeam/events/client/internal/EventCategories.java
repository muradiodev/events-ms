package com.eventeam.events.client.internal;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventCategories implements Serializable {

    String id;
    String eventId;
    List<Property> categories;

}
