package com.foodrecipes.credentials.credentials.restcontrollers;

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
import com.foodrecipes.credentials.credentials.dto.ResultResponse;
import com.foodrecipes.credentials.credentials.service.S3Service;

/**
 * Controller to handle Amazon S3-related operations such as uploading, deleting, 
 * and downloading files. It exposes RESTful APIs for interacting with S3 storage.
 */
@RestController
@RequestMapping("/s3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;  // Injecting the S3 service for handling file operations

    /**
     * Endpoint to upload a file to Amazon S3.
     * 
     * @param file Multipart file uploaded by the user.
     * @return ResultResponse indicating success or failure of the upload.
     */
    @PostMapping("/upload")
    public ResultResponse upload(@RequestParam("file") MultipartFile file) {
        if (file != null) {
            return s3Service.upload(file);
        }
        return null; // Returns null if no file is provided (Consider handling this case better)
    }

    /**
     * Endpoint to delete a file from Amazon S3.
     * 
     * @param fileName The name of the file to be deleted.
     * @return ResultResponse indicating success or failure of the deletion.
     */
    @DeleteMapping("/delete/{file}")
    public ResultResponse delete(@PathVariable("file") String fileName) {
        return s3Service.delete(fileName);
    }

    /**
     * Endpoint to download a file from Amazon S3.
     * 
     * @param fileName The name of the file to be downloaded.
     * @return ResponseEntity containing the file as an InputStreamResource along with HTTP headers.
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) {
        try {
            // Fetching the file from Amazon S3
            S3Object s3Object = s3Service.getFileFromS3(fileName);
            InputStreamResource resource = new InputStreamResource(s3Object.getObjectContent());

            // Setting response headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (AmazonS3Exception e) {
            // Handling case when the file is not found in S3
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Handling any other internal server errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
