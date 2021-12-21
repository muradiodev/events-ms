package com.eventeam.events.util.cache;

public enum RedisMapKey {
    MAP_OF_EVENT_VIEW ("map_of_event_view"),
    MAP_OF_EVENT_DETAIL_VIEW("map_of_event_details_view"),
    MAP_OF_USERS_VIEW("map_of_users_view"),
    MAP_OF_HOST_ACTIVE_EVENTS("map_of_host_active_event"),
    MAP_OF_PAST_EVENTS("map_of_past_events");


    private String mapName;

    RedisMapKey(String mapName) {
        this.mapName = mapName;
    }

    public String getMapName() {
        return mapName;
    }
}
