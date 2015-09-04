<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page session="true"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8" name="viewport"
	content="width=device-width, initial-scale=1">
<title>My Cloud Storage</title>

	<link rel="stylesheet" type="text/css"
		href="/resources/libs/bootstrap-3.1.1/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css"
		href="/resources/libs/bootstrap-dialog/css/bootstrap-dialog.min.css">
	<link rel="stylesheet" type="text/css"
		href="/resources/css/style.css">


</head>
<body>
	<div class="container-fluid">
		<div class="panel panel-default">
			<div class="panel-heading text-center">
				<h3>My Cloud Storage</h3>
			</div>
			<div class="panel-body">
				

			    <div class="text-center">
				    
				    <a class="btn btn-primary btn-custom-size" href="/upload${requestScope['javax.servlet.forward.request_uri']}" id="upload-btn"> <span class="glyphicon glyphicon-cloud-upload"></span> 
							Upload </a>	
					<a class="btn btn-primary btn-custom-size" id="newFolder-btn"> 
					<span class="glyphicon glyphicon glyphicon-folder-close"></span> 
							New folder </a>	
									      
				</div>

				<br>
				<br>
				<h4>List of All Uploaded Files</h4>
				
				${requestScope['javax.servlet.forward.request_uri']}

			</div>
			<table class="table table-hover table-condensed" id="my_table">
				<thead>
					<tr>
						<th width="5%">S.N</th>
						<th width="40%">File Name</th>
						<th width="20%">File Type</th>
						<th width="15%">File Size</th>
						<th width="10%">Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${fileList}" var="dataFile" varStatus="loopCounter">
						<tr class='clickable-row' data-path="${dataFile.location}" data-type="${dataFile.type}" data-name="${dataFile.name}">
							<td><c:out value="${loopCounter.count}" /></td>
							<td><c:out value="${dataFile.name}" /></td>
							<td><c:out value="${dataFile.type}" /></td>
							
							<td>
								<c:choose>
									<c:when test="${(dataFile.size < 1024)}">
										${dataFile.size/(1024 * 1.0)} Bytes
									</c:when>
									<c:when
										test="${(dataFile.size >= 1024) && (dataFile.size < 1024*1024)}">
										<fmt:formatNumber value="${dataFile.size/(1024 * 1.0)}"
											maxFractionDigits="2" /> KB
									</c:when>
									<c:when test="${(dataFile.size >= 1024*1024)}">
										<fmt:formatNumber value="${dataFile.size/(1024 * 1024 * 1.0)}"
											maxFractionDigits="2" /> MB
									</c:when>
								</c:choose>
							</td>
							
							<td>
								<!--  <a class="btn btn-primary" href="${pageContext.request.contextPath}/get/${dataFile.id}">
									<span class="glyphicon glyphicon-download"></span> Download </a>-->
								<!-- Single button -->
								<div class="btn-group">
								  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
								    Please Select
								    <span class="caret"></span> 
								  </button>
								  <ul class="dropdown-menu">
								    <li><a href="${pageContext.request.contextPath}/get/${dataFile.id}">Download</a></li>
								    <li><a href="${pageContext.request.contextPath}/delete/${dataFile.id}" onclick="return confirm('Are you sure you want to delete this file?');">Delete</a></li>
								    
								  </ul>
								</div>
							</td>

						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	
	 <!---------- create new folder form ---------->
    
      <!-- Modal -->
  	 <div class="modal fade" id="myModal" role="dialog">
     <div class="modal-dialog">
    
	      <!-- Modal content-->
	      <div class="modal-content">
	        <div class="modal-header">
	          <button type="button" class="close" data-dismiss="modal">&times;</button>
	          <h4><span class="glyphicon glyphicon glyphicon-folder-close"></span> Folder</h4>
	        </div>
	        <div class="modal-body" >
	          
	          <form name='newFolderForm'
			  action="<c:url value='/newFolder${requestScope["javax.servlet.forward.request_uri"]}' />" method='POST'>
			  
		            <div class="form-group">
		              <label for="foldername"> Folder Name </label>
		              <input type="text" class="form-control" placeholder="Enter your folder name" name='foldername'>
		            </div>
	
		            <button type="submit" class="btn btn-primary"><span class="glyphicon glyphicon-ok-sign"></span> CREATE</button>
		              
		             <!-- used for transfer form data, debug -->
		            <input type="hidden" name="${_csrf.parameterName}"
					value="${_csrf.token}" />
	          </form>
	        </div>

	      </div>
      
     </div>
   </div>

	<script type="text/javascript"
		src="/resources/libs/jquery/jquery-2.1.1.js"></script>
	<script type="text/javascript"
		src="/resources/libs/bootstrap-3.1.1/js/bootstrap.js"></script>
		
	<!-- script for new folder name form -->
	<script>
	$(document).ready(function(){
	    $("#newFolder-btn").click(function(){
	        $("#myModal").modal();
	    });
	    

	});
	
	//JQury for slide down table row
	$('#my_table > tbody > tr')
	 .find('td')
	 .wrapInner('<div style="display: none;" />')
	 .parent()
	 .find('td > div')
	 .slideDown(500, function(){

	  var $set = $(this);
	  $set.replaceWith($set.contents());

	 });
	
	//fade in button
	$("#upload-btn").hide().fadeIn(700);
	$("#newFolder-btn").hide().fadeIn(800);
	
	//clickable row
    $(".clickable-row").click(function() {
        
        //pass in two parameters
	  	var path = $(this).data("path");
	  	var type = $(this).data("type");
	  	var folderName = $(this).data("name");
		var folderPath = "/list" + path + "/"+folderName;
		//alert(folderPath);
		
		if(type == "Folder"){
			window.document.location = folderPath;
		}
    });
	
	
		
	</script>
</body>

</html>