package com.aeromatx.back.service;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret) {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        this.cloudinary = new Cloudinary(config);
    }

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("folder", folder);
        params.put("resource_type", "auto");
        Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), params);
        return (String) result.get("secure_url");
    }
}
