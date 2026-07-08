package org.bms.movieticketbooking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bms.movieticketbooking.entity.supporting.Notification;
import org.bms.movieticketbooking.entity.supporting.User;
import org.bms.movieticketbooking.entity.transactions.Booking;
import org.bms.movieticketbooking.enums.NotificationStatus;
import org.bms.movieticketbooking.enums.NotificationType;
import org.bms.movieticketbooking.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void sendBookingConfirmation(User user, Booking booking) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setBooking(booking);
        notification.setType(NotificationType.CONFIRMATION);
        notification.setStatus(NotificationStatus.SENT);
        notification.setScheduledAt(LocalDateTime.now());
        notificationRepository.save(notification);
        log.info("Booking confirmation sent to user: {}", user.getEmail());
    }

    public void scheduleReminder(User user, Booking booking, LocalDateTime showTime) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setBooking(booking);
        notification.setType(NotificationType.REMINDER);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setScheduledAt(showTime.minusHours(2));
        notificationRepository.save(notification);
    }
}
