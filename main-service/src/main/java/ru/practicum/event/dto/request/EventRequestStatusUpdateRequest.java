package ru.practicum.event.dto.request;

import lombok.*;
import ru.practicum.request.enums.RequestStatus;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus status;
}
