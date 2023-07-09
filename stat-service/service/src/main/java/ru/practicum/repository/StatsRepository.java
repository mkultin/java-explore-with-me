package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("select new ru.practicum.ViewStatsDto(eh.app, eh.uri, count(eh.ip)) " +
            "from EndpointHit eh " +
            "where eh.timestamp between ?1 and ?2 " +
            "and eh.uri in ?3 or ?3 is null " +
            "group by eh.app, eh.uri " +
            "order by count(eh.ip) desc")
    List<ViewStatsDto> findViewStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.ViewStatsDto(eh.app, eh.uri, count(distinct eh.ip)) " +
            "from EndpointHit eh " +
            "where eh.timestamp between ?1 and ?2 " +
            "and eh.uri in ?3 or ?3 is null " +
            "group by eh.app, eh.uri " +
            "order by count(distinct eh.ip) desc")
    List<ViewStatsDto> findViewStatsWithUnique(LocalDateTime start, LocalDateTime end, List<String> uris);
}
