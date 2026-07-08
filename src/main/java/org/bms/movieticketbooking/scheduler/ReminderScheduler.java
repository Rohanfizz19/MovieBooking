package org.bms.movieticketbooking.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bms.movieticketbooking.entity.supporting.Notification;
import org.bms.movieticketbooking.enums.NotificationStatus;
import org.bms.movieticketbooking.enums.NotificationType;
import org.bms.movieticketbooking.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderScheduler {

    private final NotificationRepository notificationRepository;

    @Scheduled(fixedRate = 300000)
    public void sendPendingReminders() {
        List<Notification> pending = notificationRepository
                .findByStatusAndTypeAndScheduledAtBefore(
                        NotificationStatus.PENDING, NotificationType.REMINDER, LocalDateTime.now());
        for (Notification notification : pending) {
            notification.setStatus(NotificationStatus.SENT);
            notificationRepository.save(notification);
            log.info("Sent reminder to user: {}", notification.getUser().getEmail());
        }
    }
}
