package com.studybuddy.demostud.Service;

import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.repository.UserRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.net.URL;

@Service
public class AvatarService {

    private final UserRepository userRepository;
    private final Storage storage = StorageOptions.getDefaultInstance().getService();
    private final String bucketName = "user-avatars-bucket";
    private final String baseUrl = "https://storage.googleapis.com/" + bucketName + "/";

    public AvatarService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String uploadAvatar(Long userId, MultipartFile file) throws IOException {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String fileName = userId + "_avatar_" + file.getOriginalFilename();

            // Создаем BlobId и BlobInfo
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

            // Загружаем файл в Cloud Storage
            storage.create(blobInfo, file.getBytes());

            // Генерируем публичный URL
            String avatarUrl = baseUrl + fileName;

            // Сохраняем путь к аватару в базе данных
            user.setAvatarPath(avatarUrl);
            userRepository.save(user);

            return avatarUrl;
        }
        throw new IOException("User not found");
    }

    public Resource getAvatar(Long userId) throws IOException {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getAvatarPath() != null) {
                try {
                    URL avatarUrl = new URL(user.getAvatarPath());
                    return new UrlResource(avatarUrl);
                } catch (MalformedURLException e) {
                    throw new IOException("Invalid URL for avatar", e);
                }
            }
        }
        throw new IOException("Avatar not found");
    }
}


