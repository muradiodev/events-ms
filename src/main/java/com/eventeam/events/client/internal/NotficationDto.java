package com.eventeam.events.client.internal;

import com.eventeam.events.client.internal.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotficationDto {

        private NotificationType topic;
        private String token;
        private String userId;
        private String userName;

}

