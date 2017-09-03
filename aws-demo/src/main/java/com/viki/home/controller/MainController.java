package com.viki.home.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.viki.home.aws.util.AWSDemoUtil;
import com.viki.home.aws.util.AWSS3Helper;

@Controller
public class MainController {

	@Value("${aws.s3.bucket.name}")
	private String bucketName;
	
	@Value("${aws.s3.folder.name}")
	private String s3FolderName;
	
	@Value("${aws.s3.demo.file.key}")
	private String fileKey;
	
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

	@RequestMapping(value = "/upload1", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody String uploadToService(
			MultipartHttpServletRequest request, HttpServletResponse response) {

		System.out.println("success1");

		return "{\"success\":1}";
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody String uploadOnSameServer(
			MultipartHttpServletRequest request, HttpServletResponse response) {

		String reslt = "File Upload Failed";
		if (request instanceof MultipartHttpServletRequest) {
			System.out.println(" Inside Multipart");

			Iterator<String> itr = ((MultipartHttpServletRequest) request)
					.getFileNames();

			MultipartFile mpf = ((MultipartHttpServletRequest) request)
					.getFile(itr.next());

			File tmpFile = new File("D:\\logs\\" + mpf.getOriginalFilename());

			try {
				mpf.transferTo(tmpFile);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			reslt = mpf.getOriginalFilename();
		}

		return "{\"success\":1}";
	}

	@RequestMapping(value = "/image", method = RequestMethod.POST)
	public @ResponseBody byte[] getFile(MultipartHttpServletRequest request) {
		try {

			System.out.println(" Inside getFile "+bucketName);
			// Retrieve image from the classpath.
			File fnew = new File("D:\\logs\\f0017792.jpg");
			BufferedImage originalImage = ImageIO.read(fnew);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(originalImage, "jpg", baos);
			byte[] imageInByte = baos.toByteArray();

			return Base64.getEncoder().encode(imageInByte);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error " + e.getMessage());
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

		} catch (Exception e) {
			logger.error("error while uploadToS3 ", e);
			throw new RuntimeException(e);
		}

		return "{\"success\"}";
	}
	
	@RequestMapping(value = "/uploadToS31", method = RequestMethod.POST)
	public @ResponseBody String uploadToS31(MultipartHttpServletRequest request) {
		try {

			String s3Dir = AWSDemoUtil.getS3Directory();
			File fileForS3Upload = AWSDemoUtil
					.getFileFromMultipartHttpServletRequest(request, s3Dir);
			AWSS3Helper.putFileInS3UsingKey(bucketName, fileKey, fileForS3Upload);

		} catch (Exception e) {
			logger.error("error while uploadToS3 ", e);
			throw new RuntimeException(e);
		}

		return "{\"success\"}";
	}
}
