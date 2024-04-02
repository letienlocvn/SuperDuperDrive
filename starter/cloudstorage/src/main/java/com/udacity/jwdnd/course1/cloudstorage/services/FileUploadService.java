package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.entity.File;
import com.udacity.jwdnd.course1.cloudstorage.entity.User;
import com.udacity.jwdnd.course1.cloudstorage.mapper.FileUploadMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileUploadService {

    private UserMapper userMapper;
    private FileUploadMapper fileUploadMapper;


    public FileUploadService(UserMapper userMapper, FileUploadMapper fileUploadMapper) {
        this.userMapper = userMapper;
        this.fileUploadMapper = fileUploadMapper;
    }

    public List<File> getAllFiles(String username) {
        Integer userId = userMapper.getUser(username).getUserId();
        if (fileUploadMapper.getAllFilesByUserId(userId).isEmpty()) {
            return new ArrayList<>();
        }
        return fileUploadMapper.getAllFilesByUserId(userId);
    }

    public int insertFile(MultipartFile multipartFile, String username) throws IOException {
        File file = new File();
        User user = userMapper.getUser(username);
        file.setContentType(multipartFile.getContentType());
        file.setFileData(multipartFile.getBytes());
        file.setFileName(multipartFile.getOriginalFilename());
        file.setFileSize(Long.toString(multipartFile.getSize()));

        return fileUploadMapper.insertFile(file, user.getUserId());

    }

    public File getFileById(Integer fileId) {
        return fileUploadMapper.findFileById(fileId);
    }

    public int deleteFile(Integer fileId) {
        return fileUploadMapper.deleteFile(fileId);
    }
}
