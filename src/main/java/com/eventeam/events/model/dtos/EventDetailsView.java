package com.eventeam.events.model.dtos;

import com.eventeam.events.client.internal.*;
import com.eventeam.events.client.internal.enums.AttendeesType;
import com.eventeam.events.model.enums.AttendanceTypeEnum;
import com.eventeam.events.model.enums.PrivacyType;
import com.eventeam.events.model.enums.TicketTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("map_of_event_details_view")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDetailsView implements Serializable {

    RequestStatus requestStatus;

    @Id
    String idEvent;
    UserView userView;
    String eventName;
    LocalDate startDate;
    LocalDate endDate;

    @JsonFormat(pattern = "HH:mm")
    LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    LocalTime endTime;

    BigDecimal duration;
    Photo coverPhoto;
    PrivacyType eventType;
    TicketTypeEnum ticketType;
    BigDecimal ticketCost;
    AttendanceTypeEnum attendanceTypeEnum;

    List<Photo> allPhoto;
    int minAttendees;
    int maxAttendees;

    @Column(length = 500)
    String description;

    BigDecimal latitude;
    BigDecimal longitude;
    String mobileNumber;
    String facebookProfile;
    String websiteUrl;
    List<Property> listOfCategories;

    @JsonProperty("usersList")
    EventUsersView eventUsersView;

    boolean attendeesPermission = true;
}
