package com.springmvc.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.springmvc.model.UploadedFile;
import com.springmvc.service.FileUploadService;

import org.apache.commons.io.FileUtils;

import org.springframework.util.FileCopyUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
public class MainController {
	/*
	@RequestMapping(value = { "/", "/welcome**" }, method = RequestMethod.GET)
	public ModelAndView defaultPage() {
 
	  ModelAndView model = new ModelAndView();
	  model.addObject("title", "Spring Security Login Form - Database Authentication");
	  model.addObject("message", "This is default page!");
	  model.setViewName("hello");
	  return model;
 
	}*/
 
	@RequestMapping(value = "/admin**", method = RequestMethod.GET)
	public ModelAndView adminPage() {
 
	  ModelAndView model = new ModelAndView();
	  model.addObject("title", "Spring Security Login Form - Database Authentication");
	  model.addObject("message", "This page is for ROLE_ADMIN only!");
	  model.setViewName("admin");
	  return model;
 
	}
 
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(@RequestParam(value = "error", required = false) String error,
		@RequestParam(value = "logout", required = false) String logout) {
 
	  ModelAndView model = new ModelAndView();
	  if (error != null) {
		model.addObject("error", "Invalid username and password!");
	  }
 
	  if (logout != null) {
		model.addObject("msg", "You've been logged out successfully.");
	  }
	  model.setViewName("login");
 
	  return model;
 
	}
 
	//for 403 access denied page
	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public ModelAndView accesssDenied() {
 
	  ModelAndView model = new ModelAndView();
 
	  //check if user is login
	  Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	  if (!(auth instanceof AnonymousAuthenticationToken)) {
		UserDetails userDetail = (UserDetails) auth.getPrincipal();	
		model.addObject("username", userDetail.getUsername());
	  }
 
	  model.setViewName("403");
	  return model;
 
	}
	
	/*-------------------------new example------------------------------------------*/
    @Autowired
    private FileUploadService uploadService;

    @RequestMapping(value = { "/", "/upload" }, method = RequestMethod.GET)
    public String home() {
          
          // will be resolved to /views/fileUploader.jsp
          return "fileUploader";
    }
 
    @RequestMapping(value = "/uploadFiles", method = RequestMethod.POST)
    public @ResponseBody
    List<UploadedFile> upload(MultipartHttpServletRequest request,
                 HttpServletResponse response) throws IOException {

    	  System.out.println("in upload");
          // Getting uploaded files from the request object
          Map<String, MultipartFile> fileMap = request.getFileMap();

          // Maintain a list to send back the files info. to the client side
          List<UploadedFile> uploadedFiles = new ArrayList<UploadedFile>();

          // Iterate through the map
          for (MultipartFile multipartFile : fileMap.values()) {

                 // Save the file to local disk
                 saveFileToLocalDisk(multipartFile);

                 UploadedFile fileInfo = getUploadedFileInfo(multipartFile);

                 // Save the file info to database
                 fileInfo = saveFileToDatabase(fileInfo);

                 // adding the file info to the list
                 uploadedFiles.add(fileInfo);
          }

          return uploadedFiles;
    }

    @RequestMapping(value = { "/list" })
    public String listBooks(Map<String, Object> map) {

          map.put("fileList", uploadService.listFiles());

          // will be resolved to /views/listFiles.jsp
          return "listFiles";
    }

    @RequestMapping(value = "/get/{fileId}", method = RequestMethod.GET)
    public void getFile(HttpServletResponse response, @PathVariable Long fileId) {

          UploadedFile dataFile = uploadService.getFile(fileId);

          File file = new File(dataFile.getLocation(), dataFile.getName());

          try {
                 response.setContentType(dataFile.getType());
                 response.setHeader("Content-disposition", "attachment; filename=\""
                              + dataFile.getName() + "\"");

                 FileCopyUtils.copy(FileUtils.readFileToByteArray(file),
                              response.getOutputStream());

          } catch (IOException e) {
                 e.printStackTrace();
          }
    }

    private void saveFileToLocalDisk(MultipartFile multipartFile)
                 throws IOException, FileNotFoundException {

          String outputFileName = getOutputFilename(multipartFile);

          FileCopyUtils.copy(multipartFile.getBytes(), new FileOutputStream(
                       outputFileName));
    }

    private UploadedFile saveFileToDatabase(UploadedFile uploadedFile) {

          return uploadService.saveFile(uploadedFile);

    }

    private String getOutputFilename(MultipartFile multipartFile) {

          return getDestinationLocation() + multipartFile.getOriginalFilename();
    }

    private UploadedFile getUploadedFileInfo(MultipartFile multipartFile)
                 throws IOException {

          UploadedFile fileInfo = new UploadedFile();
          fileInfo.setName(multipartFile.getOriginalFilename());
          fileInfo.setSize(multipartFile.getSize());
          fileInfo.setType(multipartFile.getContentType());
          fileInfo.setLocation(getDestinationLocation());

          return fileInfo;
    }

    private String getDestinationLocation() {
          return "/home/jxy/uploaded-files/";
    }    
}
