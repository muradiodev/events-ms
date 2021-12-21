package com.eventeam.events.model.dtos;

import com.eventeam.events.client.internal.EventUsersView;
import com.eventeam.events.client.internal.Photo;
import com.eventeam.events.model.enums.AttendanceTypeEnum;
import com.eventeam.events.model.enums.PrivacyType;
import com.eventeam.events.model.enums.TicketTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("map_of_event_view")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventView implements Serializable {

    @Id
    String idEvent;
    String eventName;
    LocalDate startDate;
    LocalDate endDate;

    @JsonFormat(pattern = "HH:mm")
    LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    LocalTime endTime;
    BigDecimal duration;
    Photo coverPhoto;
    BigDecimal latitude;
    BigDecimal longitude;

    @Enumerated(value = EnumType.STRING)
    AttendanceTypeEnum attendanceTypeEnum;

    @Enumerated (value = EnumType.STRING)
    TicketTypeEnum ticketType;

    @JsonProperty("usersList")
    EventUsersView eventUsersView;

    boolean attendeesPermission = true;

}
