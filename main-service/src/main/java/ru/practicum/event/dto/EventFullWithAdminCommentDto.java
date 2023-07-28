package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.enums.State;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserShortDto;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EventFullWithAdminCommentDto extends EventFullDto {
    @Size(max = 7000)
    private String adminComment;

    @Builder(builderMethodName = "EventFullWithAdminCommentDtoBuilder")
    public EventFullWithAdminCommentDto(Long id, String title, String annotation, String description,
                                        CategoryDto category, UserShortDto initiator,
                                        LocalDateTime eventDate, LocalDateTime createdOn, LocalDateTime publishedOn,
                                        Location location, Boolean paid, Integer participantLimit,
                                        Boolean requestModeration, Integer confirmedRequests, State state,
                                        Long views, String adminComment) {
        super(id, title, annotation, description, category, initiator, eventDate, createdOn, publishedOn,
                location, paid, participantLimit, requestModeration, confirmedRequests, state, views);
        this.adminComment = adminComment;
    }
}
