package com.pymerstan.server.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    private Path photoStorageLocation;
    private Path cvStorageLocation;

    @PostConstruct
    public void init() {
        this.photoStorageLocation = Paths.get(uploadDir, "photos").toAbsolutePath().normalize();
        this.cvStorageLocation = Paths.get(uploadDir, "cvs").toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.photoStorageLocation);
            Files.createDirectories(this.cvStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the upload directories.", ex);
        }
    }

    public String savePhoto(MultipartFile file) {
        return saveFileToDisk(file, this.photoStorageLocation);
    }

    public String saveCv(MultipartFile file) {
        return saveFileToDisk(file, this.cvStorageLocation);
    }

    private String saveFileToDisk(MultipartFile file, Path targetLocation) {
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
            return null;
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        try {
            Path targetPath = targetLocation.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return uniqueFilename;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFilename + ". Please try again!", ex);
        }
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex >= 0) {
            return filename.substring(dotIndex);
        }
        return "";
    }

    // --- Added Resource Loading Methods for FileController ---

    public Resource loadPhotoAsResource(String fileName) {
        return loadFileAsResource(fileName, this.photoStorageLocation);
    }

    public Resource loadCvAsResource(String fileName) {
        return loadFileAsResource(fileName, this.cvStorageLocation);
    }

    private Resource loadFileAsResource(String fileName, Path storageLocation) {
        try {
            Path filePath = storageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or not readable: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found: " + fileName, ex);
        }
    }
}