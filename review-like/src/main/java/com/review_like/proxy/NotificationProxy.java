package com.review_like.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.review_like.dto.NotificationRequest;

@FeignClient(name = "notification")
public interface NotificationProxy {
	@PostMapping("/notification/send")
    ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request);
}
