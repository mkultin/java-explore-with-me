package ru.practicum.service;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.exception.ValidationException;

@UtilityClass
public class CheckPagination {
    public PageRequest getPageByParams(Integer from, Integer size) {
        if (from < 0 || size < 0) throw new ValidationException("Переданы неверные параметры пагинации");
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }

    public PageRequest getPageByParamsWithSortParam(Integer from, Integer size, String sortBy) {
        if (from < 0 || size < 0) throw new ValidationException("Переданы неверные параметры пагинации");
        Sort sort;
        if (sortBy != null) {
            switch (sortBy) {
                case "EVENT_DATE":
                    sort = Sort.by("eventDate");
                    break;
                case "VIEWS":
                    sort = Sort.by("views");
                    break;
                default:
                    throw new ValidationException("Параметр сортировки " + sortBy + " не доступен. " +
                            "Доступные параметры сортироввки: EVENT_DATE, VIEWS");
            }
        } else return getPageByParams(from, size);
        return PageRequest.of(from > 0 ? from / size : 0, size, sort);
    }

    public PageRequest getPageByParamsWithSort(Integer from, Integer size, String sortBy) {
        if (from < 0 || size < 0) throw new ValidationException("Переданы неверные параметры пагинации");
        Sort sort = Sort.by(sortBy);
        return PageRequest.of(from > 0 ? from / size : 0, size, sort);
    }
}
