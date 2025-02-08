package com.foodrecipes.amazonservices.awscontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import com.foodrecipes.amazonservices.response.ResultResponse;
import com.foodrecipes.amazonservices.service.S3Service;


@RestController
@RequestMapping("/s3")
public class S3Controller {
	@Autowired
	private S3Service s3Service;
	
	@PostMapping("/upload")
	public ResultResponse upload(@RequestParam("file") MultipartFile file) {
		if(file!=null)
			return s3Service.upload(file);
		return null;
	}
	
	
	
	@DeleteMapping("/delete/{file}")
	public ResultResponse delete(@PathVariable("file") String fileName) {
		return s3Service.delete(fileName);
	}
	
	
	
	@GetMapping("/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) {
        try {
            S3Object s3Object = s3Service.getFileFromS3(fileName);
            InputStreamResource resource = new InputStreamResource(s3Object.getObjectContent());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (AmazonS3Exception e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }	
}
