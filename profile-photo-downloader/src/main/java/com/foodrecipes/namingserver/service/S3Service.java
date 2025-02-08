package com.foodrecipes.namingserver.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.foodrecipes.namingserver.response.MetadataResponse;
import com.foodrecipes.namingserver.response.ResultResponse;


@Service
public class S3Service {
	@Value("${aws.bucket.profile}")
	private String bucketProfileName;
	
    private final AmazonS3 s3Client;
	public S3Service(AmazonS3 s3) {
		this.s3Client = s3;
	}
	
	public ResultResponse download(String fileName) {
        ResultResponse response = new ResultResponse();
        MetadataResponse metadataResponse = new MetadataResponse();

        try {
            // Get the S3 object
            S3Object s3Object = getFileFromS3(fileName);

            metadataResponse.setCode("200");
            metadataResponse.setMessage("File downloaded successfully");
            metadataResponse.setNoOfRecords("1");
            response.setMetadataResponse(metadataResponse);
            response.setResult(s3Object.getObjectContent()); // or s3Object.getObjectContent().getRedirectLocation()

            return response;
        } catch (AmazonS3Exception e) {
            metadataResponse.setCode("400");
            metadataResponse.setMessage("File not found in S3");
            metadataResponse.setNoOfRecords("0");
            response.setMetadataResponse(metadataResponse);
            response.setResult(null);

            return response;
        }
    }

    public S3Object getFileFromS3(String fileName) throws AmazonS3Exception {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketProfileName, fileName);
        return s3Client.getObject(getObjectRequest);
    }
	
}
