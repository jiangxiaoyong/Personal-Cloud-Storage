package com.springmvc.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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
	
	/*------------------------------------------------new example------------------------------------------*/
    @Autowired
    private FileUploadService uploadService;
    
    @RequestMapping(value = "/newFolder/**", method = RequestMethod.POST)
    public String newFolder(HttpServletRequest request, HttpServletResponse response,@RequestParam("foldername") String foldername){
          String entirePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
    	  if(foldername != null){
    		  // Save the folder to local disk
    		  saveFolderToLocalDisk(foldername, entirePath);
    		  
    		  UploadedFile folderInfo = getUploadedFolderInfo(foldername, entirePath);
    		  
    		  // Save the folder info to database
    		  saveFolderToDatabase(folderInfo);
    	  }
    	  return "redirect:/list";
    }

    @RequestMapping(value = { "upload" }, method = RequestMethod.GET)
    public String home(HttpServletRequest request, HttpServletResponse response) {
          
    	  System.out.println("request in upload" + request.getRequestURL().toString());
          // will be resolved to /views/fileUploader.jsp
          return "fileUploader";
    }
 
    @RequestMapping(value = "/uploadFiles", method = RequestMethod.POST)
    public
    String upload(MultipartHttpServletRequest request,
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

          return "listFiles";
    }

    @RequestMapping(value = { "/list/**" })
    public ModelAndView listBooks(HttpServletRequest request, HttpServletResponse response) {
    	  String entirePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
    	  System.out.println("In list entire path= " + entirePath);
    	  
    	  String folderPath = getFolderPath(entirePath.split("/"));
    	  if(folderPath.isEmpty()){
    		  folderPath = "jxy/uploaded-files";
    	  }
    	  System.out.println("foder path= " + folderPath);
    	  List<UploadedFile> query_result = uploadService.listDocs(folderPath);
    	  for(UploadedFile uf : query_result){
    		  System.out.println("query result = " + uf.getLocation());
    	  }
    	  
          ModelAndView model = new ModelAndView();
          model.addObject("fileList", uploadService.listDocs(folderPath));
          model.setViewName("listFiles");

          return model;
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
    
    @RequestMapping(value = "/delete/{fileId}")
    public String deleteFile(@PathVariable("fileId") Long id){

    	  UploadedFile dataFile = uploadService.getFile(id);	  	  
	  	  String deleteFileName = getDeleteFilename(dataFile.getName());
	  	  System.out.println("delete path = " + deleteFileName);
	  	  
	  	  //delete record in MySQL
    	  uploadService.deleteFile(id);
    	  
    	  //delete stored on local disc
    	  deleteFileFromLocalDisk(deleteFileName);
    	  return "redirect:/list";

    }
    private void deleteFileFromLocalDisk(String path){
    	try{
    		
    		File file = new File(path);
        	
    		if(file.delete()){
    			System.out.println(file.getName() + " is deleted!");
    		}else{
    			System.out.println("Delete operation is failed.");
    		}
    	   
    	}catch(Exception e){
    		
    		e.printStackTrace();
    		
    	}
    }
    
    private void saveFolderToLocalDisk(String foldername, String entirePath){
	  	  // parse the path string
    	  System.out.println("save folder raw path = " + entirePath);
	  	  String[] splitedPath = entirePath.split("/");
	
	  	  //get new path to create new folder
	  	  String path = getNewfolderPath(splitedPath);
	  	  System.out.println("parent folder = " + path);
	  	  System.out.println("foldername " +foldername);
  	  
	  	  //create new folder
		  File theDir = new File(path + "/"+foldername);
		
		  // if the directory does not exist, create it
		  if (!theDir.exists()) {
			    System.out.println("creating directory: " + theDir);
			    boolean result = false;
			
			    try{
			        theDir.mkdir();
			        result = true;
			    } 
			    catch(SecurityException se){
			        //handle it
			    }        
			    if(result) {    
			        System.out.println("DIR created");  
			    }
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
    
    private UploadedFile saveFolderToDatabase(UploadedFile uploadedFolder) {

        return uploadService.saveFile(uploadedFolder);

  }

    private String getOutputFilename(MultipartFile multipartFile) {

          return getDestinationLocation() + "/" + multipartFile.getOriginalFilename();
    }
    
    private String getDeleteFilename(String file) {

        return getDestinationLocation() + "/" +file;
    }
    
    private String getNewfolderPath(String[] str){
    	
    	String newStr = "";
    	for(int i = 3; i < str.length; i++){
    		newStr += "/" + str[i];
    	}
    	
    	//return getDestinationLocation() + newStr;
    	if(newStr.isEmpty()){
    		newStr = "/home/jxy/uploaded-files";
    	}
    	return newStr;
    }
    
    private String getFolderPath(String[] str){
    	String newStr = "";
    	for(int i = 2; i < str.length; i++){
    		newStr += str[i] + "/";
    	}
    	if(newStr.length() > 1){
        	newStr = newStr.substring(0, newStr.length()-1);
    	}
  	
    	return newStr;
    }

    private UploadedFile getUploadedFolderInfo(String foldername, String entirePath){
    	
    	  UploadedFile folderInfo = new UploadedFile();
    	  folderInfo.setName(foldername);
    	  //folderInfo.setSize((long) 0);
    	  folderInfo.setType("Folder");
    	  folderInfo.setLocation(getNewfolderPath(entirePath.split("/")));
    	  
    	  return folderInfo;
    	
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
          return "/home/jxy/uploaded-files";
    }    
}
