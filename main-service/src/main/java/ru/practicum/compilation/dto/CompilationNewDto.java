package ru.practicum.compilation.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.service.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
public class CompilationNewDto {
    private List<Long> events;
    private Boolean pinned = false;
    @NotBlank(groups = Marker.Create.class)
    @Size(groups = {Marker.Create.class, Marker.Update.class}, min = 1, max = 50)
    private String title;
}
