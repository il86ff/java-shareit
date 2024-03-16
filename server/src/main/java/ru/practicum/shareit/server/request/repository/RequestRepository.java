package ru.practicum.shareit.server.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.server.request.entity.ItemRequest;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequester_IdOrderByCreatedDesc(Long id);

    List<ItemRequest> findAllByRequesterIdNot(Long userId, Pageable pageable);
}
