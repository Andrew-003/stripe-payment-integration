package com.my.payments.service.interfaces;

import com.my.payments.pojo.NotificationRequest;

public interface NotificationService {
	
	public void processNotification(NotificationRequest notificationRequest);

}
