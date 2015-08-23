package com.springmvc.service;

import java.util.List;
import com.springmvc.model.UploadedFile;

public interface FileUploadService {
        List<UploadedFile> listFiles();
        UploadedFile getFile(Long id);
        void deleteFile(Long id);
        UploadedFile saveFile(UploadedFile uploadedFile);
}
