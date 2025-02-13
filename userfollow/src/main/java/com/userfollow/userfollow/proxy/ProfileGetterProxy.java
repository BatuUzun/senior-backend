package com.userfollow.userfollow.proxy;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.userfollow.userfollow.dto.UserProfileResponseDTO;

@FeignClient(name = "profile-getter")
public interface ProfileGetterProxy {
	
    @PostMapping("/profile-getter/fetch-ids")
    ResponseEntity<List<UserProfileResponseDTO>> getUserProfiles(@RequestBody List<Long> userIds);
    
}
