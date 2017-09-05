package com.viki.home.aws.util;

/*
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

/**
 * This sample demonstrates how to make basic requests to Amazon SQS using the
 * AWS SDK for Java.
 *
 * Prerequisites: You must have a valid Amazon Web Services developer account,
 * and be signed up to use Amazon SQS. For more information about Amazon SQS,
 * see http://aws.amazon.com/sqs
 * 
 * Fill in your AWS access credentials in the provided credentials file
 * template, and be sure to move the file to the default location
 * (~/.aws/credentials) where the sample code loads the credentials from.
 * 
 * IMPORTANT: To avoid accidental leakage of your credentials, DO NOT keep the
 * credentials file in your source directory.
 */
public class AWSSQSHelper {

	private static final Logger logger = Logger.getLogger(AWSSQSHelper.class);

	public static void createQueueAndSendMessageToSQS(String message, String queueNm) {
		final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
		
		try {
			CreateQueueResult create_result = sqs.createQueue(queueNm);
			logger.info("createQueueAndSendMessageToSQS create_result");
		} catch (AmazonSQSException e) {
			if (!e.getErrorCode().equals("QueueAlreadyExists")) {
				throw e;
			}
		}

		String queueUrl = sqs.getQueueUrl(queueNm).getQueueUrl();

		SendMessageRequest send_msg_request = new SendMessageRequest()
				.withQueueUrl(queueUrl).withMessageBody(message)
				.withDelaySeconds(5);
		sqs.sendMessage(send_msg_request);

		logger.info("createQueueAndSendMessageToSQS send_msg_request "
				+ message);
	}

	public static void sendMessageToSQS(String message, String queueNm) {
		final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
		String queueUrl = sqs.getQueueUrl(queueNm).getQueueUrl();
		SendMessageRequest send_msg_request = new SendMessageRequest()
				.withQueueUrl(queueUrl).withMessageBody(message)
				.withDelaySeconds(5);
		sqs.sendMessage(send_msg_request);
	}

	public static List<String> receiveMessagesFromSQS(String queueNm) {
		final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
		String queueUrl = sqs.getQueueUrl(queueNm).getQueueUrl();;
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl).withAttributeNames("All");
		
		receiveMessageRequest.setMaxNumberOfMessages(10);

		List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		logger.info("receiveMessagesFromSQS messages sizenew " + messages.size());
		// delete messages from the queue
		for (Message m : messages) {
			logger.info("receiveMessagesFromSQS before deletion  "
					+ m.getBody());
			sqs.deleteMessage(queueUrl, m.getReceiptHandle());
			logger.info("receiveMessagesFromSQS deleteMessage ");
		}

		List<String> messageStrList = new ArrayList<String>();

		for (Message m : messages) {
			messageStrList.add(m.getBody());
			logger.info("receiveMessagesFromSQS message body " + m.getBody());
		}

		logger.info("receiveMessagesFromSQS returning");
		return messageStrList;
	}
}