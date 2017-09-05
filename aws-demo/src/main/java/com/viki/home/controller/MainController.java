package com.viki.home.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.viki.home.aws.util.AWSDemoUtil;
import com.viki.home.aws.util.AWSS3Helper;
import com.viki.home.aws.util.AWSSNSHelper;
import com.viki.home.aws.util.AWSSQSHelper;
import com.viki.home.aws.util.Demo;

@Controller
public class MainController {

	@Value("${aws.s3.bucket.name}")
	private String bucketName;
	
	@Value("${aws.s3.folder.name}")
	private String s3FolderName;
	
	@Value("${aws.s3.demo.file.key}")
	private String fileKey;
	
	@Value("${local.directory}")
	private String localDir;
	
	@Value("${aws.sqs.queue.name}")
	private String queueNm;
	
	private static final Logger logger = Logger.getLogger(MainController.class);

	@Bean
	public WebMvcConfigurerAdapter forwardToIndex() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addViewControllers(ViewControllerRegistry registry) {
				// forward requests to /admin and /user to their index.html
				registry.addViewController("/admin").setViewName(
						"forward:/admin/index.html");
				registry.addViewController("/").setViewName(
						"forward:/index.html");
			}
		};
	}

	@RequestMapping(value = "/uploadAndRetrieveFromLocalMachine", method = RequestMethod.POST)
	public @ResponseBody String uploadAndRetrieveFromLocalMachine(MultipartHttpServletRequest request) {
		try {

			// Retrieve image from the classpath
			File fnew = AWSDemoUtil.getFileFromMultipartHttpServletRequest(request, localDir);
			//File fnew = request.getFile(arg0)
			BufferedImage originalImage = ImageIO.read(fnew);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(originalImage, "jpg", baos);
			byte[] imageInByte = baos.toByteArray();

			return DatatypeConverter.printBase64Binary(imageInByte);

		} catch (Exception e) {
			logger.error("error while uploadAndRetrieveFromLocalMachine ", e);
			throw new RuntimeException(e);
		}
	}

	@RequestMapping(value = "/uploadToS3", method = RequestMethod.POST)
	public @ResponseBody String uploadToS3(MultipartHttpServletRequest request) {
		try {

			String s3Dir = AWSDemoUtil.getS3Directory();
			File fileForS3Upload = AWSDemoUtil
					.getFileFromMultipartHttpServletRequest(request, s3Dir);
			AWSS3Helper.putFileInS3(fileForS3Upload, bucketName, s3FolderName);
			
			String keyName = AWSS3Helper.getKeyNameForFileUsingFolder(fileForS3Upload, s3FolderName);
			logger.info("keyName "+keyName);
			byte[] imageInByte = AWSS3Helper.getObjectFromS3(bucketName, keyName);

			return DatatypeConverter.printBase64Binary(imageInByte);

		} catch (Exception e) {
			logger.error("error while uploadToS3 ", e);
			throw new RuntimeException(e);
		}
	}
	
	@RequestMapping(value = "/sendToSQS", method = RequestMethod.POST)
	public @ResponseBody String sendToSQS(@RequestBody Demo request) {
		try {
			String subject = request.getSubject();
			String content = request.getMessage();
			
			logger.info("request "+subject+ " content "+content );
			AWSSQSHelper.sendMessageToSQS(content, queueNm);
			
			AWSSQSHelper.receiveMessagesFromSQS(queueNm);
			
			return "{\"result\":\"success\"}";

		} catch (Exception e) {
			logger.error("error while sendToSQS ", e);
			throw new RuntimeException(e);
		}
	}
	
	@RequestMapping(value = "/sendToSNS", method = RequestMethod.POST)
	public @ResponseBody String sendToSNS(@RequestBody Demo request) {
		try {
			String subject = request.getSubject();
			String content = request.getMessage();
			
			logger.info("request "+subject+ " content "+content );
			AWSSNSHelper.publishToSNSTopic(content);
			
			//AWSSQSHelper.receiveMessagesFromSQS();
			
			return "{\"result\":\"success\"}";

		} catch (Exception e) {
			logger.error("error while sendToSNS ", e);
			throw new RuntimeException(e);
		}
	}
}
