package ru.practicum.shareit.server.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.server.booking.entity.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long id, Pageable pageable);

    List<Booking> findByItem_User_IdOrderByStartDesc(Long id, Pageable pageable);

    List<Booking> findByItem_User_IdOrderByEndDesc(Long id);

    List<Booking> findByItem_IdOrderByEndDesc(Long itemId);

    List<Booking> findByBooker_IdAndItem_Id(Long userId, Long itemId);
}
