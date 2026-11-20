package com.iharshashkou.internetstore.service.customDesign;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CustomDesignImageStorageService {

    private final Path storageDirectory;

    public CustomDesignImageStorageService(
            @Value("${app.storage.custom-designs-dir}") String storageDirectory
    ) {
        this.storageDirectory = Paths.get(storageDirectory)
                .toAbsolutePath()
                .normalize();
    }

    public String saveImage(Long designId, byte[] imageBytes) {
        try {
            Files.createDirectories(storageDirectory);

            String fileName = designId + ".png";
            Path targetPath = storageDirectory.resolve(fileName).normalize();

            if (!targetPath.startsWith(storageDirectory)) {
                throw new IllegalArgumentException("Недопустимый путь к файлу");
            }

            Files.write(targetPath, imageBytes);

            return "/api/custom-designs/" + designId + "/image";

        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Не удалось сохранить изображение проекта", ex
            );
        }
    }

    public byte[] getImage(Long designId) {
        try {
            String fileName = designId + ".png";

            Path imagePath = storageDirectory
                    .resolve(fileName)
                    .normalize();

            if (!imagePath.startsWith(storageDirectory)) {
                throw new IllegalArgumentException(
                        "Недопустимый путь к файлу"
                );
            }

            if (!Files.exists(imagePath) || !Files.isRegularFile(imagePath)) {
                throw new EntityNotFoundException(
                        "Изображение проекта не найдено"
                );
            }

            return Files.readAllBytes(imagePath);

        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Не удалось прочитать изображение проекта", ex
            );
        }
    }
}
