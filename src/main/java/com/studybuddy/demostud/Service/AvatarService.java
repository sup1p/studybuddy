package com.studybuddy.demostud.Service;

import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.repository.UserRepository;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class AvatarService {

    private final UserRepository userRepository;
    private final String uploadDir = "uploads/avatars/";
    private final String baseUrl = "http://localhost:8080/avatars/";

    public AvatarService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String uploadAvatar(Long userId, MultipartFile file) throws IOException {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String fileName = userId + "_avatar_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());
            String avatarUrl = baseUrl + fileName;
            user.setAvatarPath(avatarUrl);
            userRepository.save(user);
            return avatarUrl;
        }
        throw new IOException("User not found");
    }

    public String updateAvatar(Long userId, MultipartFile file) throws IOException {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Удалить старый аватар, если он существует
            if (user.getAvatarPath() != null) {
                Path oldFilePath = Paths.get(uploadDir + user.getAvatarPath().replace(baseUrl, ""));
                Files.deleteIfExists(oldFilePath);
            }
            // Загрузить новый аватар
            return uploadAvatar(userId, file);
        }
        throw new IOException("User not found");
    }

    public Resource getAvatar(Long userId) throws IOException {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getAvatarPath() != null) {
                Path filePath = Paths.get(uploadDir + user.getAvatarPath().replace(baseUrl, ""));
                if (Files.exists(filePath)) {
                    return new PathResource(filePath);
                }
            }
        }
        throw new IOException("Avatar not found");
    }
}

