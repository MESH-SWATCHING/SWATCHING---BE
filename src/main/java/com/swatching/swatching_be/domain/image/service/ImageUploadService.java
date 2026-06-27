package com.swatching.swatching_be.domain.image.service;

import com.swatching.swatching_be.domain.image.dto.ImageUploadResponse;
import com.swatching.swatching_be.domain.user.User;
import com.swatching.swatching_be.global.exception.BusinessException;
import com.swatching.swatching_be.global.exception.ErrorCode;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class ImageUploadService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final Duration VIEW_URL_EXPIRES_IN = Duration.ofMinutes(10);

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucket;
    private final String region;
    private final String publicBaseUrl;

    public ImageUploadService(
            S3Client s3Client,
            S3Presigner s3Presigner,
            @Value("${app.aws.s3.bucket}") String bucket,
            @Value("${app.aws.s3.region}") String region,
            @Value("${app.aws.s3.public-base-url}") String publicBaseUrl
    ) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucket = bucket;
        this.region = region;
        this.publicBaseUrl = publicBaseUrl;
    }

    public ImageUploadResponse uploadImage(User user, MultipartFile file) {
        validateS3Config();
        validateFile(file);

        String contentType = file.getContentType();
        String objectKey = createObjectKey(user, file.getOriginalFilename(), contentType);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(contentType)
                .contentLength(file.getSize())
                .build();

        try {
            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException exception) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to read uploaded image.");
        } catch (S3Exception exception) {
            throw new BusinessException(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "Failed to upload image to S3. bucket=%s, region=%s, awsMessage=%s".formatted(
                            bucket,
                            region,
                            exception.awsErrorDetails().errorMessage()
                    )
            );
        }

        return new ImageUploadResponse(
                createImageUrl(objectKey),
                objectKey,
                contentType,
                file.getSize()
        );
    }

    public String createViewUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return imageUrl;
        }
        if (publicBaseUrl != null && !publicBaseUrl.isBlank() && imageUrl.startsWith(publicBaseUrl)) {
            return imageUrl;
        }

        String objectKey = extractObjectKey(imageUrl);
        if (objectKey == null || objectKey.isBlank()) {
            return imageUrl;
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(VIEW_URL_EXPIRES_IN)
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public List<String> createViewUrls(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return List.of();
        }
        return imageUrls.stream()
                .map(this::createViewUrl)
                .toList();
    }

    private void validateS3Config() {
        if (bucket == null || bucket.isBlank()) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "S3 bucket is not configured.");
        }
    }

    private void validateContentType(String contentType) {
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Only jpeg, png, and webp images can be uploaded.");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Image file is required.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Image file must be 10MB or less.");
        }
        validateContentType(file.getContentType());
    }

    private String createObjectKey(User user, String fileName, String contentType) {
        return "users/%d/images/%s%s".formatted(
                user.getId(),
                UUID.randomUUID(),
                resolveExtension(fileName, contentType)
        );
    }

    private String resolveExtension(String fileName, String contentType) {
        String normalized = fileName == null ? "" : fileName.toLowerCase(Locale.ROOT).trim();
        if (normalized.endsWith(".jpg") || normalized.endsWith(".jpeg")) {
            return ".jpg";
        }
        if (normalized.endsWith(".png")) {
            return ".png";
        }
        if (normalized.endsWith(".webp")) {
            return ".webp";
        }
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }

    private String createImageUrl(String objectKey) {
        String encodedKey = URLEncoder.encode(objectKey, StandardCharsets.UTF_8).replace("+", "%20").replace("%2F", "/");
        if (publicBaseUrl != null && !publicBaseUrl.isBlank()) {
            return publicBaseUrl.replaceAll("/+$", "") + "/" + encodedKey;
        }
        return "https://%s.s3.%s.amazonaws.com/%s".formatted(bucket, region, encodedKey);
    }

    private String extractObjectKey(String imageUrl) {
        try {
            URI uri = URI.create(imageUrl);
            String host = uri.getHost();
            if (host == null) {
                return null;
            }

            String expectedHost = "%s.s3.%s.amazonaws.com".formatted(bucket, region);
            if (!expectedHost.equals(host)) {
                return null;
            }

            String path = uri.getRawPath();
            if (path == null || path.length() <= 1) {
                return null;
            }
            return URLDecoder.decode(path.substring(1), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
