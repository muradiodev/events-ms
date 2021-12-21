package com.eventeam.events.client.internal;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestStatus {
    private String id;
    private String status;
    private String idFromUser;
    private String idToUser;
}
