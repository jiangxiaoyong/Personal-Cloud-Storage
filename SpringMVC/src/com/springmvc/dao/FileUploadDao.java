package com.springmvc.dao;

import java.util.List;

import com.springmvc.model.UploadedFile;

public interface FileUploadDao {
        List<UploadedFile> listFiles();
        List<UploadedFile> listDocs(String folderName);
        List<UploadedFile> getAllDeleteFiles(String folderName);
        UploadedFile getFile(Long id);
        void deleteFile(Long id);
        UploadedFile saveFile(UploadedFile uploadedFile);
}