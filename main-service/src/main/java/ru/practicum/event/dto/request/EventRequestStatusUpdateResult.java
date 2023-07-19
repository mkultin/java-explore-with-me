package ru.practicum.event.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.request.dto.EventRequestDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class EventRequestStatusUpdateResult {
    private final List<EventRequestDto> confirmedRequests = new ArrayList<>();
    private final List<EventRequestDto> rejectedRequests = new ArrayList<>();
}
