package com.simplified_pic_pay.service.transaction;

import com.simplified_pic_pay.domain.user.User;
import com.simplified_pic_pay.dtos.NotificatioDTO;
import com.simplified_pic_pay.exception.NotificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationTransaction {

    @Autowired
    private RestTemplate restTemplate;

    public boolean notifyTransaction(User user, String message) {
        var email = user.getEmail();
        NotificatioDTO notification = new NotificatioDTO(email, message);

        String url = "https://util.devi.tools/api/v1/notify";
        ResponseEntity<String> notificationResponse = restTemplate.postForEntity(url, notification, String.class);

        if (notificationResponse.getBody() == null || !notificationResponse.getBody().contains("success")) {
            throw new NotificationException("Notification service unavailable");
        }
        return true;
    }
}
