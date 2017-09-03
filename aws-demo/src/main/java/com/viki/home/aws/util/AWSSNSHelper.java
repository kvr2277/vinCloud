package com.viki.home.aws.util;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.DeleteTopicRequest;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;

public class AWSSNSHelper {
	
	private static final Logger logger = Logger.getLogger(AWSSNSHelper.class);
	
	public static void createSNSTopic(){
		
		//create a new SNS client and set endpoint
		AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();	

		//create a new SNS topic
		CreateTopicRequest createTopicRequest = new CreateTopicRequest("MyNewTopic");
		CreateTopicResult createTopicResult = snsClient.createTopic(createTopicRequest);
		//print TopicArn
		logger.info("topicArn "+createTopicResult);
		//get request id for CreateTopicRequest from SNS metadata		
		logger.info("CreateTopicRequest - " + snsClient.getCachedResponseMetadata(createTopicRequest));
	}
	
	public static void subscribeSNSTopic(){
		
		AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();
		
		final Properties props = new Properties();
		try {
			props.load(new FileInputStream("/var/www/html/app.properties"));
			
		} catch (Exception e) {
			logger.info("Exception ", e);
			
		}
		String topicArn = props.getProperty("topic.arn");
		String email = props.getProperty("topic.email");
		
		//subscribe to an SNS topic
		SubscribeRequest subRequest = new SubscribeRequest(topicArn, "email", email);
		snsClient.subscribe(subRequest);
		//get request id for SubscribeRequest from SNS metadata
		logger.info("SubscribeRequest - " + snsClient.getCachedResponseMetadata(subRequest));
		logger.info("Check your email and confirm subscription.");
	}
	
	public static void publishToSNSTopic(String msg){
		
		AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();
		
		final Properties props = new Properties();
		try {
			props.load(new FileInputStream("/var/www/html/app.properties"));
			
		} catch (Exception e) {
			logger.info("Exception ", e);
			
		}
		String topicArn = props.getProperty("topic.arn");
		//publish to an SNS topic
		
		PublishRequest publishRequest = new PublishRequest(topicArn, msg);
		PublishResult publishResult = snsClient.publish(publishRequest);
		//print MessageId of message published to SNS topic
		logger.info("MessageId - " + publishResult.getMessageId());
		
	}
	
	public static void deleteSNSTopic(){
		
		AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();
		
		final Properties props = new Properties();
		try {
			props.load(new FileInputStream("/var/www/html/app.properties"));
			
		} catch (Exception e) {
			logger.info("Exception ", e);
			
		}
		String topicArn = props.getProperty("topic.arn");
		
		//delete an SNS topic
		DeleteTopicRequest deleteTopicRequest = new DeleteTopicRequest(topicArn);
		snsClient.deleteTopic(deleteTopicRequest);
		//get request id for DeleteTopicRequest from SNS metadata
		logger.info("DeleteTopicRequest - " + snsClient.getCachedResponseMetadata(deleteTopicRequest));
		
	}

}
