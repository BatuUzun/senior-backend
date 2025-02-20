package com.foodrecipes.namingserver.controller;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.foodrecipes.namingserver.service.S3Service;

/**
 * Controller responsible for downloading profile pictures from Amazon S3.
 * This API fetches an image from S3 and returns it in Base64 format.
 */
@RestController
@RequestMapping("/profile-picture-downloader/")
public class S3Controller {

    @Autowired
    private S3Service s3Service;
    
    @Autowired
    private Environment environment;

    /**
     * Downloads an image from S3 and converts it to a Base64-encoded string.
     *
     * @param imageName The name of the image file stored in S3.
     * @return A Base64-encoded string representation of the image.
     * @throws IOException If an error occurs while reading the image.
     */
    @GetMapping("/download/{fileName}")
    public String getImageBase64(@PathVariable("fileName") String imageName) throws IOException {
        // Fetch the image object from Amazon S3
        S3Object imageObject = s3Service.getFileFromS3(imageName);

        // Convert the image content to a byte array
        byte[] imageBytes = IOUtils.toByteArray(imageObject.getObjectContent());

        // Encode the byte array to Base64 format
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        // Log the bucket name for debugging purposes
        System.out.println(imageObject.getBucketName());

        // Return the Base64-encoded image string
        return base64Image;
    }
}
