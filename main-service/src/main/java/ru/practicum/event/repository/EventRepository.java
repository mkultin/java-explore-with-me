package ru.practicum.event.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.event.enums.State;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findEventsByInitiatorIdOrderById(Long userId, PageRequest pageRequest);

    @Query("select e " +
            "from Event e " +
            "where (:users is null or e.initiator.id in :users) " +
            "and (:states is null or e.state in :states) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and ((cast(:rangeStart as timestamp) is null or cast(:rangeEnd as timestamp) is null) " +
            "or (cast(:rangeStart as timestamp) is not null and cast(:rangeEnd as timestamp) is not null " +
            "and e.eventDate between cast(:rangeStart as timestamp) and cast(:rangeEnd as timestamp)))")
    List<Event> findEventsForAdmin(List<Long> users, List<State> states, List<Long> categories,
                                   LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest pageRequest);

    @Query("select e " +
            "from Event e " +
            "where (e.state = :state) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (:paid is null or e.paid = :paid) " +
            "and ((cast(:rangeStart as timestamp) is null or cast(:rangeEnd as timestamp) is null) " +
            "or (cast(:rangeStart as timestamp) is not null and cast(:rangeEnd as timestamp) is not null " +
            "and e.eventDate between :rangeStart and :rangeEnd)) " +
            "and (:text is null or lower(e.annotation) like concat('%', :text, '%') " +
            "or lower(e.description) like concat('%', :text, '%'))")
    List<Event> findEventsForPublic(State state, String text, List<Long> categories, Boolean paid,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                    PageRequest pageRequest);
}
