package com.viki.home.aws.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AWSS3Helper {

	private static final String SUFFIX = "/";
	private static final Logger logger = Logger.getLogger(AWSS3Helper.class);


	public static void putFileInS3(File file, String bucketName, String folderName) {
		logger.info("Inside putFileInS3");

		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		logger.info("After creating  s3client");
		// create bucket - name must be unique for all S3 users
		s3client.createBucket(bucketName);

		// list buckets
		for (Bucket bucket : s3client.listBuckets()) {
			logger.info(" - " + bucket.getName());
		}

		// create folder into bucket
		createFolder(bucketName, folderName, s3client);

		// upload file to folder and set it to public
		String keyName = getKeyNameForFileUsingFolder(file, folderName); 
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyName, file);
		PutObjectResult putObjectResult = s3client.putObject(putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead));
				
		logger.info("uploaded file in s3 bucketName "+bucketName+" folderName "+folderName+" keyName "+keyName);

	}
	
	public static void createFolder(String bucketName, String folderName,
			AmazonS3 client) {
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
				folderName + SUFFIX, emptyContent, metadata);
		// send request to S3 to create folder
		client.putObject(putObjectRequest);
	}

	/**
	 * This method first deletes all the files in given folder and than the
	 * folder itself
	 */
	public static void deleteFolder(String bucketName, String folderName,
			AmazonS3 client) {
		List<S3ObjectSummary> fileList = client.listObjects(bucketName,
				folderName).getObjectSummaries();
		for (S3ObjectSummary file : fileList) {
			client.deleteObject(bucketName, file.getKey());
		}
		client.deleteObject(bucketName, folderName);
	}

	public static String getKeyNameForFileUsingFolder(File file, String folderName){
		return folderName + SUFFIX + file.getName();		
	}
	
	public static byte[] getObjectFromS3(String bucketName, String keyName) throws IOException {
		
		AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();       
		S3Object object = s3Client.getObject(
		                  new GetObjectRequest(bucketName, keyName));
		InputStream objectData = object.getObjectContent();
		// Process the objectData stream.
		objectData.close();
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int next = objectData.read();
		while (next > -1) {
		    bos.write(next);
		    next = objectData.read();
		}
		bos.flush();
		byte[] result = bos.toByteArray();
		
		return result;
	}
}
