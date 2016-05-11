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
          
          String folderPath = getNewfolderPath(entirePath.split("/"));
          String redirectPage = "redirect:/list" + folderPath;
    	  if(foldername != null){
    		  // Save the folder to local disk
    		  saveFolderToLocalDisk(foldername, entirePath);
    		  
    		  UploadedFile folderInfo = getUploadedFolderInfo(foldername, entirePath);
    		  
    		  // Save the folder info to database
    		  saveFolderToDatabase(folderInfo);
    	  }
    	  return redirectPage;
    }

    @RequestMapping(value = { "/upload/**" }, method = RequestMethod.GET)
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) {
  	      String rawPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
    	  System.out.println("request in upload first round " + rawPath);
    	  
          String folderPath = getNewfolderPath(rawPath.split("/"));
          System.out.println("request in upload first round refined " + folderPath);
    	  
    	  ModelAndView model = new ModelAndView();
    	  model.addObject("uploadPath", (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE));
    	  model.addObject("folderPath", folderPath);
    	  model.setViewName("fileUploader");
    	  return model;
    }
 
    @RequestMapping(value = "/uploadFiles/**", method = RequestMethod.POST)
    public
    String upload(MultipartHttpServletRequest request,
                 HttpServletResponse response) throws IOException {

    	  String rawUploadPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
    	  System.out.println("request in upload second round" + rawUploadPath);
    	  
    	  String uploadFolderPath = getUploadFolderPath(rawUploadPath.split("/"));
    	  System.out.println("uploadFolderPath " + uploadFolderPath );
    	  
          // Getting uploaded files from the request object
          Map<String, MultipartFile> fileMap = request.getFileMap();

          // Maintain a list to send back the files info. to the client side
          List<UploadedFile> uploadedFiles = new ArrayList<UploadedFile>();

          // Iterate through the map
          for (MultipartFile multipartFile : fileMap.values()) {

                 // Save the file to local disk
                 saveFileToLocalDisk(multipartFile, uploadFolderPath);

                 UploadedFile fileInfo = getUploadedFileInfo(multipartFile,uploadFolderPath);

                 // Save the file info to database
                 fileInfo = saveFileToDatabase(fileInfo);

                 // adding the file info to the list
                 uploadedFiles.add(fileInfo);
          }

          return "redirect:/list";
    }

    @RequestMapping(value = { "/list/**" })
    public ModelAndView listBooks(HttpServletRequest request, HttpServletResponse response) {
    	  String entirePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
    	  System.out.println("In list, entire path= " + entirePath);
    	  
    	  String folderPath = getNewfolderPath(entirePath.split("/"));

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
	  	  System.out.println("delte id = " + id);
	  	  System.out.println("delete location = " + dataFile.getLocation());
	  	  String returnLocation = "redirect:/list" + dataFile.getLocation();
	  	  
	  	  //delete record in MySQL
	  	  deleteFileFromeDatabase(dataFile.getName(),id);
    	    	  
    	  //delete stored on local disc
    	  deleteFileFromLocalDisk(dataFile.getName(), dataFile.getLocation());
    	  return returnLocation;

    }
    
    private void deleteFileFromeDatabase(String folderName, Long id){
    	//delete folder it self
    	uploadService.deleteFile(id);
    	
    	//delete recursively
    	List<UploadedFile> query_result = uploadService.getAllDeleteFiles(folderName);
	  	for(UploadedFile uf : query_result){
			  System.out.println("query result in Delete = " + uf.getId() + " Delete it !");
			  uploadService.deleteFile(uf.getId());
		}
    	
    }
    private void deleteFileFromLocalDisk(String docName, String path){

    	try{

        	System.out.println("delete full path " + path + docName);
        	
    		File doc = new File(path + "/"+docName);
        	
    		if(!doc.exists()){
    			System.out.println("Directory does not exist.");
    			
    		}else{    			
    			delete(doc);			
    		}
    	   
    	}catch(Exception e){
    		
    		e.printStackTrace();
			System.out.println("Delete operation is failed.");

    	}
    }
    
    private void delete(File doc) throws IOException {
    	
    	if(doc.isDirectory()){
        	FileUtils.deleteDirectory(doc);
        	System.out.println(doc.getName() + " is deleted!");
    	}
    	else{
    		doc.delete();
    		System.out.println(doc.getName() + " is deleted!");
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

    private void saveFileToLocalDisk(MultipartFile multipartFile,String folderPath)
                 throws IOException, FileNotFoundException {

          String outputFileName = getOutputFilename(multipartFile, folderPath);
          System.out.println("outputFileName "  + outputFileName);
          FileCopyUtils.copy(multipartFile.getBytes(), new FileOutputStream(
                       outputFileName));
    }

    private UploadedFile saveFileToDatabase(UploadedFile uploadedFile) {

          return uploadService.saveFile(uploadedFile);

    }
    
    private UploadedFile saveFolderToDatabase(UploadedFile uploadedFolder) {

        return uploadService.saveFile(uploadedFolder);

  }

    private String getOutputFilename(MultipartFile multipartFile, String folderPath) {

          return getDestinationLocation() + "/" + folderPath + multipartFile.getOriginalFilename();
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
    		newStr = "/var/local/uploaded-files";
			new File("/var/local/uploaded-files").mkdirs();
    	}
    	return newStr;
    }
    
    private String getUploadFolderPath(String[]str){
    	
    	String newStr = "";
    	for(int i = 7; i < str.length; i++){
    		newStr += str[i] + "/";
    	}
    	
    	return newStr;
    }
    
    private String getStoreLocation(String uploadFolderPath){
    	
    	String s = getDestinationLocation() + "/" + uploadFolderPath;
    	s = s.substring(0, s.length()-1);
    	return s;
    }

    private UploadedFile getUploadedFolderInfo(String foldername, String entirePath){
    	
    	  UploadedFile folderInfo = new UploadedFile();
    	  folderInfo.setName(foldername);
    	  //folderInfo.setSize((long) 0);
    	  folderInfo.setType("Folder");
    	  folderInfo.setLocation(getNewfolderPath(entirePath.split("/")));
    	  
    	  return folderInfo;
    	
    }
    private UploadedFile getUploadedFileInfo(MultipartFile multipartFile, String uploadFolderPath)
                 throws IOException {

          UploadedFile fileInfo = new UploadedFile();
          fileInfo.setName(multipartFile.getOriginalFilename());
          fileInfo.setSize(multipartFile.getSize());
          fileInfo.setType(multipartFile.getContentType());
          fileInfo.setLocation(getStoreLocation(uploadFolderPath));

          return fileInfo;
    }

    private String getDestinationLocation() {
          return "/var/local/uploaded-files";
    }    
}
