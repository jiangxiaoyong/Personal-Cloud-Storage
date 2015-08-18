<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page session="true"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html lang="en">
<head>

<meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

	<title>Cloud Storage</title>

 
	<link rel="stylesheet" type="text/css" href="resources/libs/bootstrap-3.1.1/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="resources/libs/bootstrap-dialog/css/bootstrap-dialog.min.css">
	<link rel="stylesheet" type="text/css" href="resources/css/style.css">

</head>
<body>
       <div class="container">
              <div class="panel panel-default">
                     <div class="panel-heading text-center">
                           <h3>My Cloud Storage</h3>
                     </div>
                     <div class="panel-body">
                           <div>
                                  <form id="dropzone-form" class="dropzone" method="post" action="./upload?${_csrf.parameterName}=${_csrf.token}"
                                         enctype="multipart/form-data">

                                         <div class="dz-default dz-message file-dropzone text-center well col-sm-12">
                                                 <span class="glyphicon glyphicon-paperclip"></span> <span>
                                                       To attach files, drag and drop here</span><br>
                                                <span>OR</span><br>
                                                <span>Just Click</span>
                                         </div>

                                         <!-- this is were the previews should be shown. -->
                                         <div class="dropzone-previews"></div>
										 
                                  </form>
                                  <hr>
                                  <button id="upload-button" class="btn btn-primary">
                                         <span class="glyphicon glyphicon-upload"></span> Upload
                                  </button>
                                  <a class="btn btn-primary pull-right" href="list"> <span
                                         class="glyphicon glyphicon-eye-open"></span> View All Uploads
                                  </a>
                           </div>
                     </div>
              </div>
       </div>

       <script type="text/javascript"
              src="resources/libs/jquery/jquery-2.1.1.js"></script>
       <script type="text/javascript"
              src="resources/libs/bootstrap-3.1.1/js/bootstrap.js"></script>
       <script type="text/javascript"
              src="resources/libs/bootstrap-dialog/js/bootstrap-dialog.min.js"></script>
       <script type="text/javascript"
              src="resources/libs/dropzone.js"></script>
       <script type="text/javascript"
              src="resources/js/app.js"></script>
</body>
</html>
