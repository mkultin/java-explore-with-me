package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.request.enums.RequestStatus;

import java.time.LocalDateTime;

import static ru.practicum.service.Constants.PATTERN;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestDto {
    private Long id;
    private Long requester;
    private Long event;
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime created;
    private RequestStatus status;
}
