package com.eventeam.events.model.dtos;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("map_of_host_active_event")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HostActiveEvents implements Serializable {

    @Id
    String id;

    List<String> hostActiveEventsIds;
}
