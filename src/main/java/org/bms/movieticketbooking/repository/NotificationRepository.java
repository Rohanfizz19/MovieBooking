package org.bms.movieticketbooking.repository;

import org.bms.movieticketbooking.entity.supporting.Notification;
import org.bms.movieticketbooking.enums.NotificationStatus;
import org.bms.movieticketbooking.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByStatusAndTypeAndScheduledAtBefore(
            NotificationStatus status, NotificationType type, LocalDateTime before);
}
