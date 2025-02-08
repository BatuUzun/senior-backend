package com.foodrecipes.amazonservices.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.foodrecipes.amazonservices.constants.Constants;
import com.foodrecipes.amazonservices.response.MetadataResponse;
import com.foodrecipes.amazonservices.response.ResultResponse;

@Service
public class S3Service {
	@Value("${aws.bucket.profile}")
	private String bucketProfileName;
	
	
    private final AmazonS3 s3Client;
	public S3Service(AmazonS3 s3) {
		this.s3Client = s3;
	}
	
	public ResultResponse upload(MultipartFile file) {
        MetadataResponse metadataResponse = new MetadataResponse();
        ResultResponse response = new ResultResponse();
        File fileSave = null;
        try {
            fileSave = convertMultiPartToFile(file);
            String uniqueFilename = UUID.randomUUID().toString() + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

            s3Client.putObject(new PutObjectRequest(bucketProfileName, uniqueFilename, fileSave));

            metadataResponse.setCode("200");
            metadataResponse.setMessage("File is uploaded");
            metadataResponse.setNoOfRecords("1");
            response.setMetadataResponse(metadataResponse);
            response.setResult(uniqueFilename);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            metadataResponse.setCode("400");
            metadataResponse.setMessage("File is NOT uploaded: " + e.getMessage());
            metadataResponse.setNoOfRecords("0");
            response.setMetadataResponse(metadataResponse);
            response.setResult(null);
            return response;
        } finally {
            if (fileSave != null && fileSave.exists()) {
                fileSave.delete();
            }
        }
    }
	
	
	
	
	
	public ResultResponse delete(String file) {
		MetadataResponse metadataResponse = new MetadataResponse();
		ResultResponse response = new ResultResponse();
		if(!file.equals(Constants.DEFAULT_PROFILE_IMAGE)) {
			
		try {
			
			s3Client.deleteObject(bucketProfileName, file);
			
			metadataResponse.setCode("200");
			metadataResponse.setMessage("File is deleted");
			metadataResponse.setNoOfRecords("1");
			response.setMetadataResponse(metadataResponse);
			response.setResult(file);
			return response;
			
		} catch (Exception e) {
			metadataResponse.setCode("400");
			metadataResponse.setMessage("File is NOT deleted");
			metadataResponse.setNoOfRecords("0");
			response.setMetadataResponse(metadataResponse);
			response.setResult(null);
			return response;
		}
		}
		else {
			metadataResponse.setCode("400");
			metadataResponse.setMessage("You cannot delete "+Constants.DEFAULT_PROFILE_IMAGE+" file");
			metadataResponse.setNoOfRecords("0");
			response.setMetadataResponse(metadataResponse);
			response.setResult(null);
			return response;
		}
		
	}
	
	
	
	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}
	
	public S3Object getFileFromS3(String fileName) throws AmazonS3Exception {
        return s3Client.getObject(bucketProfileName, fileName);
    }
	
}
