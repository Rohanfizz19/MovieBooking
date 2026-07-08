package org.bms.movieticketbooking.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bms.movieticketbooking.repository.ShowSeatRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatHoldCleanupScheduler {

    private final ShowSeatRepository showSeatRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void releaseExpiredHolds() {
        int released = showSeatRepository.releaseExpiredHolds(LocalDateTime.now());
        if (released > 0) {
            log.info("Released {} expired seat holds", released);
        }
    }
}
