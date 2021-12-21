package com.eventeam.events.mapper;

import com.eventeam.events.db.entity.Events;
import com.eventeam.events.model.dtos.EventDetailsView;
import com.eventeam.events.model.dtos.EventView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public abstract class EventsMapper {

    public static final EventsMapper INSTANCE = Mappers.getMapper(EventsMapper.class);

    @Mappings(
            {
                    @Mapping(target = "idEvent", source = "idEvents"),
                    @Mapping(target = "coverPhoto.idPhoto", source = "idCoverPhoto"),
                    @Mapping(target = "userView.id", source = "idUser")
            }
    )
    public abstract EventDetailsView mapToDetailsView(Events events);

    @Mappings(
            {
                    @Mapping(target = "idEvents", source = "idEvent"),
                    @Mapping(source = "coverPhoto.idPhoto", target = "idCoverPhoto"),
                    @Mapping(source = "userView.id", target = "idUser")
            }
    )
    public abstract Events mapToEvent(EventDetailsView eventDetailsView);

    @Mappings(
            {
                    @Mapping(target = "idEvent", source = "idEvents"),
                    @Mapping(target = "coverPhoto.idPhoto", source = "idCoverPhoto"),
            }
    )
    public abstract EventView mapToEventView(Events events);

    @Mappings(
            {
                    @Mapping(target = "idEvents", source = "idEvent"),
                    @Mapping(source = "coverPhoto.idPhoto", target = "idCoverPhoto"),
            }
    )
    public  abstract Events mapToEvent(EventView eventView);


}
