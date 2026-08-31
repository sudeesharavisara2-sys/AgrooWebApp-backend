package com.agroo.agroo.service.impl;

import com.agroo.agroo.exception.FileStorageException;
import com.agroo.agroo.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir:uploads/products}")
    private String uploadDir;

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".gif", ".webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    public String storeFile(MultipartFile file) {
        validateFile(file);

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + fileExtension;

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Extract the subfolder name (e.g., "products") dynamically to prepend it
            String folderName = Paths.get(uploadDir).getFileName().toString();

            // Returns a relative path like "products/uuid-filename.jpg"
            return folderName + "/" + filename;

        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + e.getMessage());
        }
    }

    @Override
    public List<String> storeFiles(List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(storeFile(file));
        }
        return urls;
    }

    // අලුතින් එකතු කළ Method එක: සම්පූර්ණ URL එකෙන් File නම පමණක් වෙන් කර ගැනීමට
    private String extractRelativePath(String fileUrl) {
        if (fileUrl == null) return null;
        // URL එකක් ආවොත්, '/uploads/' වලින් පස්සේ තියෙන ටික විතරක් ගන්නවා
        if (fileUrl.startsWith("http")) {
            int index = fileUrl.indexOf("/uploads/");
            if (index != -1) {
                return fileUrl.substring(index + "/uploads/".length());
            }
        }
        return fileUrl; // කලින්ම හරි විදිහට (උදා: products/file.png) ආවොත් ඒකම යවනවා
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            String relativePath = extractRelativePath(fileUrl);
            if (relativePath != null) {
                Path filePath = Paths.get("uploads").resolve(relativePath);
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete file: " + e.getMessage());
        }
    }

    @Override
    public void deleteFiles(List<String> fileUrls) {
        if (fileUrls == null) return;
        for (String fileUrl : fileUrls) {
            deleteFile(fileUrl);
        }
    }

    @Override
    public boolean fileExists(String fileUrl) {
        try {
            String relativePath = extractRelativePath(fileUrl);
            if (relativePath != null) {
                Path filePath = Paths.get("uploads").resolve(relativePath);
                return Files.exists(filePath);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("File is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            throw new FileStorageException("File has no name");
        }

        String extension = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new FileStorageException("File type not allowed. Allowed: " + ALLOWED_EXTENSIONS);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileStorageException("File too large. Max size: " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }
    }
}