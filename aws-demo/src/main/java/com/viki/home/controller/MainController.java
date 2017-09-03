package com.viki.home.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Controller
public class MainController {

   /* @RequestMapping(value="/",method = RequestMethod.GET)
    public String homepage(){
        return "index";
    }*/
    
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
	public @ResponseBody
	String uploadToService(MultipartHttpServletRequest request,
			HttpServletResponse response) {

		System.out.println("success1");
		
		return "{\"success\":1}";
	}
    
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody
	String uploadOnSameServer(MultipartHttpServletRequest request,
			HttpServletResponse response) {

		String reslt = "File Upload Failed";
		if (request instanceof MultipartHttpServletRequest) {
			System.out.println(" Inside Multipart");

			Iterator<String> itr = ((MultipartHttpServletRequest) request)
					.getFileNames();

			MultipartFile mpf = ((MultipartHttpServletRequest) request)
					.getFile(itr.next());

			File tmpFile = new File("D:\\logs\\"
					+ mpf.getOriginalFilename());

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
    public @ResponseBody byte[] getFile(MultipartHttpServletRequest request)  {
        try {
        	
        	System.out.println(" Inside getFile");
            // Retrieve image from the classpath.
        	File fnew=new File("D:\\logs\\f0017792.jpg");
        	BufferedImage originalImage=ImageIO.read(fnew);
        	ByteArrayOutputStream baos=new ByteArrayOutputStream();
        	ImageIO.write(originalImage, "jpg", baos );
        	byte[] imageInByte=baos.toByteArray();
        	
        	return Base64.getEncoder().encode(imageInByte);
            
        } catch (Exception e) {
        	e.printStackTrace();
        	System.out.println("Error "+e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
