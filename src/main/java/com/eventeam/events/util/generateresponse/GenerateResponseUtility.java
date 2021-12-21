package com.eventeam.events.util.generateresponse;

import com.eventeam.events.client.internal.*;
import com.eventeam.events.model.ResponseData;
import com.eventeam.events.model.dtos.EventDetailsView;
import com.eventeam.events.model.dtos.EventView;

import java.util.List;
import java.util.stream.Collectors;

public class GenerateResponseUtility {

    public static GenerateResponse<Integer, String, EventDetailsView, ResponseData<EventDetailsView>> eventDetailFunc = (code, message, data) ->
            ResponseData.<EventDetailsView>builder()
                    .code(code)
                    .message(message)
                    .body(data)
                    .build();

    public static GenerateResponse<Integer, String, List<EventDetailsView>, ResponseData<List<EventDetailsView>>> eventDetailListFunc = (code, message, list) ->
            ResponseData.<List<EventDetailsView>>builder()
                    .code(code)
                    .message(message)
                    .body(list)
                    .build();

    public static GenerateResponse<Integer, String, EventView, ResponseData<EventView>> eventFunc = (code, message, data) ->
            ResponseData.<EventView>builder()
                    .code(code)
                    .message(message)
                    .body(data)
                    .build();

    public static GenerateResponse<Integer, String, List<EventView>, ResponseData<List<EventView>>> eventListFunc = (code, message, list) ->
            ResponseData.<List<EventView>>builder()
                    .code(code)
                    .message(message)
                    .body(list)
                    .build();

    public static GenerateResponse<Integer, String, String, ResponseData<String>> stringFunc = (code, message, data) ->
            ResponseData.<String>builder()
                    .code(code)
                    .message(message)
                    .body(data)
                    .build();

    public static GenerateResponse<Integer, String, List<String>, ResponseData<List<String>>> stringListFunc = (code, message, list) ->
            ResponseData.<List<String>>builder()
                    .code(code)
                    .message(message)
                    .body(list)
                    .build();
}
