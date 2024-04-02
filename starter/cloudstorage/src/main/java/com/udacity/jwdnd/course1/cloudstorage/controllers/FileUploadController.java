package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.entity.File;
import com.udacity.jwdnd.course1.cloudstorage.services.FileUploadService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class FileUploadController {

    private FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/file")
    public String handleUploadFile(Authentication authentication, MultipartFile fileUpload,
                                   RedirectAttributes redirectAttributes) throws IOException {
        String message;
        int rowEffected;
        if (fileUpload.isEmpty()) {
            message = "Please select a file to upload.";
            rowEffected = -1;
        } else {
            rowEffected = fileUploadService.insertFile(fileUpload, authentication.getName());
            if (rowEffected > 0) {
                message = "File uploaded successfully.";
            } else {
                message = "An error occurred while uploading the file. Please try again.";
            }
        }

        redirectAttributes.addFlashAttribute((rowEffected > 0) ? "successMessage" : "errorMessage", message);
        return "redirect:/result";
    }

    @GetMapping("/files/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable(name = "fileId") Integer fileId) {
        File file = fileUploadService.getFileById(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\"")
                .body(new ByteArrayResource(file.getFileData()));
    }

    @GetMapping("files/delete")
    public String deleteFile(@RequestParam("fileId") Integer fileId, RedirectAttributes redirectAttributes) {
        int rowEffected = fileUploadService.deleteFile(fileId);
        String message;
        if (rowEffected > 0) {
            message = "Delete file successfully.";
        } else {
            message = "An error occurred while deleting the file. Please try again.";
        }

        redirectAttributes.addFlashAttribute((rowEffected > 0) ? "successMessage" : "errorMessage", message);
        return "redirect:/result";
    }

}
