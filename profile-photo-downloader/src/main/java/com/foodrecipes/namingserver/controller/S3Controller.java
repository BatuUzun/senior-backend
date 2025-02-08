package com.foodrecipes.namingserver.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.foodrecipes.namingserver.service.S3Service;

@RestController
@RequestMapping("/profile-picture-downloader/")
public class S3Controller {
	@Autowired
	private S3Service s3Service;
	@Autowired
	private Environment environment;
	
	@GetMapping("/download/{fileName}")
	public String getImageBase64(@PathVariable("fileName") String imageName) throws IOException {
	    S3Object imageObject = s3Service.getFileFromS3(imageName);
	    byte[] imageBytes = IOUtils.toByteArray(imageObject.getObjectContent());
	    
	    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
	    System.out.println(imageObject.getBucketName());
	    return base64Image;
	}
	
	
}