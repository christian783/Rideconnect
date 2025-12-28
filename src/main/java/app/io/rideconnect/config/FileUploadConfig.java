package app.io.rideconnect.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "file.upload")
@Data
public class FileUploadConfig {
    private String uploadDir = "uploads";
    private Long maxFileSize = 10485760L; // 10MB
    private String[] allowedExtensions = {"jpg", "jpeg", "png", "pdf"};
}