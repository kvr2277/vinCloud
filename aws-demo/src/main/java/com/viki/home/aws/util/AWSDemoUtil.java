package com.viki.home.aws.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public class AWSDemoUtil {

	private static final Logger logger = Logger.getLogger(AWSDemoUtil.class);

	public static File getFileFromMultipartHttpServletRequest(
			MultipartHttpServletRequest request, String directory)
			throws IOException {
		
		
		
		File file = null;
		if (request instanceof MultipartHttpServletRequest) {
			logger.info("inside MultipartHttpServletRequest ");

			Iterator<String> fileNameIterator = ((MultipartHttpServletRequest) request)
					.getFileNames();

			if (fileNameIterator.hasNext()) {
				MultipartFile mpf = ((MultipartHttpServletRequest) request)
						.getFile(fileNameIterator.next());

				file = new File(directory, mpf.getOriginalFilename());
				file.createNewFile();
				logger.info("created new file in system ");
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				fileOutputStream.write(mpf.getBytes());
				logger.info("written content to system file at " + directory);
				fileOutputStream.close();
			}
		}

		return file;
	}

	public static String getS3Directory() throws FileNotFoundException,
			IOException {
		final Properties props = new Properties();
		props.load(new FileInputStream("/var/www/html/app.properties"));
		String directory = props.getProperty("temp.directory");

		return directory;

	}

}
