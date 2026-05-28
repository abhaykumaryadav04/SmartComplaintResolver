package com.smartcomplaint.smartcompaint.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.smartcomplaint.smartcompaint.exception.BadRequestException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private final Path uploadDir;

    public FileStorageService(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Only JPEG, PNG, and WEBP images are supported");
        }
        try {
            Files.createDirectories(uploadDir);
            @SuppressWarnings("null")
            String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "image" : file.getOriginalFilename());
            String extension = original.contains(".") ? original.substring(original.lastIndexOf(".")) : ".jpg";
            String filename = UUID.randomUUID() + extension;
            Files.copy(file.getInputStream(), uploadDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + filename;
        } catch (IOException ex) {
            throw new BadRequestException("Could not store uploaded image");
        }
    }
}
