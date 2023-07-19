package ru.practicum.event.model;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
@Getter
@ToString
public class Location {
    @NotNull
    private Float lat;
    @NotNull
    private Float lon;
}
