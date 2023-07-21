package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.enums.State;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.service.Constants.PATTERN;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventFullDto {
    private Long id;
    private String title;
    private String annotation;
    private String description;
    private CategoryDto category;
    private UserShortDto initiator;
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime eventDate;
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime createdOn;
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime publishedOn;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private Integer confirmedRequests;
    private State state;
    private Long views;
}
