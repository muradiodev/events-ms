package com.eventeam.events.client.internal;

import com.eventeam.events.client.internal.enums.AttendeesType;
import com.eventeam.events.client.internal.enums.EventUserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventUsers {

    private String idEvent;
    private String idUser;
    private EventUserType eventUserType;
    private AttendeesType attendeesType;


}
