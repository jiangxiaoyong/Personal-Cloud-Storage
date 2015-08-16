package com.springmvc.dao.impl;

import java.util.List;

import com.springmvc.dao.FileUploadDao;
import com.springmvc.model.UploadedFile;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FileUploadDaoImpl implements FileUploadDao {

      @Autowired
      private SessionFactory sessionFactory;

      public List<UploadedFile> listFiles() {
             return getSession().createCriteria(UploadedFile.class).list();
      }

      public UploadedFile getFile(Long id) {
             return (UploadedFile) getSession().get(UploadedFile.class, id);
      }

      public UploadedFile saveFile(UploadedFile uploadedFile) {
             return (UploadedFile) getSession().merge(uploadedFile);
      }

      private Session getSession() {
             Session sess = getSessionFactory().getCurrentSession();
             if (sess == null) {
                    sess = getSessionFactory().openSession();
             }
             return sess;
      }

      private SessionFactory getSessionFactory() {
             return sessionFactory;
      }
}