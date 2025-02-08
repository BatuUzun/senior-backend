package com.foodrecipes.profileapi.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import com.foodrecipes.profileapi.config.FeignConfig;
import com.foodrecipes.profileapi.response.ResultResponse;

@FeignClient(name = "amazon-services", configuration = FeignConfig.class)
public interface Amazons3Proxy {
	
    @PostMapping(value = "/s3/upload", consumes = "multipart/form-data")
	ResultResponse upload(@RequestPart("file") MultipartFile file);
	
    @DeleteMapping("/s3/delete/{file}")
	ResultResponse delete(@PathVariable("file") String fileName);
    
}
