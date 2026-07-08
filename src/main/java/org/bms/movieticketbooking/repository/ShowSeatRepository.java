package org.bms.movieticketbooking.repository;

import jakarta.persistence.LockModeType;
import org.bms.movieticketbooking.entity.content.ShowSeat;
import org.bms.movieticketbooking.enums.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {

    List<ShowSeat> findByShowId(Long showId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ss FROM ShowSeat ss WHERE ss.id IN :ids")
    List<ShowSeat> findByIdsWithLock(@Param("ids") List<Long> ids);

    @Modifying
    @Query("UPDATE ShowSeat ss SET ss.status = 'AVAILABLE', ss.heldUntil = null, ss.heldByUserId = null " +
            "WHERE ss.status = 'HELD' AND ss.heldUntil < :now")
    int releaseExpiredHolds(@Param("now") LocalDateTime now);
}
