package com.eventeam.events.client.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Photo implements Serializable {

    String idPhoto;
    String photo;

    @JsonProperty("userId")
    String eventId;

}
