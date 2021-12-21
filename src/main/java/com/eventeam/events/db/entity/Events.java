package com.eventeam.events.db.entity;

import com.eventeam.events.model.dtos.UserView;
import com.eventeam.events.model.enums.AttendanceTypeEnum;
import com.eventeam.events.model.enums.PrivacyType;
import com.eventeam.events.model.enums.EventStatus;
import com.eventeam.events.model.enums.TicketTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Events {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(255)")
    String idEvents;

    String eventName;
    LocalDate startDate;
    LocalDate endDate;

    @JsonFormat(pattern = "HH:mm")
    LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    LocalTime endTime;
    String idUser;

    @Enumerated(value = EnumType.STRING)
    PrivacyType eventType;

    @Column(length = 500)
    String description;

    BigDecimal duration;
    String idCoverPhoto;
    int minAttendees;
    int maxAttendees;
    BigDecimal latitude;
    BigDecimal longitude;
    String mobileNumber;
    String facebookProfile;
    String websiteUrl;
    int premiumOrder;

    @Enumerated (value = EnumType.STRING)
    TicketTypeEnum ticketType;

    private BigDecimal ticketCost;

    @Enumerated (value = EnumType.STRING)
    private EventStatus processStatus;

    @Enumerated (value = EnumType.STRING)
    private AttendanceTypeEnum attendanceTypeEnum;

    int cached;

    boolean attendeesPermission = true;
}
