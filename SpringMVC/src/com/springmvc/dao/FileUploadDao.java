package com.springmvc.dao;

import java.util.List;
import com.springmvc.model.UploadedFile;

public interface FileUploadDao {
        List<UploadedFile> listFiles();
        UploadedFile getFile(Long id);
        UploadedFile saveFile(UploadedFile uploadedFile);
}