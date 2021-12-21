package com.eventeam.events.client.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Property implements Serializable {

    @JsonIgnore
    String id;

    String name;
    String keyword;

    @JsonIgnore
    String parentId;

}

