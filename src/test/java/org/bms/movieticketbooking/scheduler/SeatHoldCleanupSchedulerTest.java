package org.bms.movieticketbooking.scheduler;

import org.bms.movieticketbooking.repository.ShowSeatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatHoldCleanupSchedulerTest {

    @Mock
    private ShowSeatRepository showSeatRepository;

    @InjectMocks
    private SeatHoldCleanupScheduler scheduler;

    @Test
    void releaseExpiredHolds_callsRepository() {
        when(showSeatRepository.releaseExpiredHolds(any(LocalDateTime.class))).thenReturn(3);

        scheduler.releaseExpiredHolds();

        verify(showSeatRepository).releaseExpiredHolds(any(LocalDateTime.class));
    }

    @Test
    void releaseExpiredHolds_noExpired() {
        when(showSeatRepository.releaseExpiredHolds(any(LocalDateTime.class))).thenReturn(0);

        scheduler.releaseExpiredHolds();

        verify(showSeatRepository).releaseExpiredHolds(any(LocalDateTime.class));
    }
}
