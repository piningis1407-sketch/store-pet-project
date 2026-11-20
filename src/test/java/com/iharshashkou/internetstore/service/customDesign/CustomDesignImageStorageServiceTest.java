package com.iharshashkou.internetstore.service.customDesign;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CustomDesignImageStorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void savesImageAndReturnsCorrectUrl() throws IOException {
        // Arrange
        Path storageDir = tempDir.resolve("custom-designs");
        CustomDesignImageStorageService service =
                new CustomDesignImageStorageService(storageDir.toString());

        Long designId = 1L;
        byte[] imageBytes = {1, 2, 3, 4, 5};

        // Act
        String imageUrl = service.saveImage(designId, imageBytes);

        // Assert
        assertEquals(
                "/api/custom-designs/1/image",
                imageUrl
        );

        Path savedImage = storageDir.resolve("1.png");

        assertTrue(Files.exists(savedImage));
        assertArrayEquals(imageBytes, Files.readAllBytes(savedImage));
    }

    @Test
    void readsPreviouslySavedImage() {
        // Arrange
        CustomDesignImageStorageService service =
                new CustomDesignImageStorageService(tempDir.toString());

        Long designId = 2L;
        byte[] imageBytes = {10, 20, 30, 40};

        service.saveImage(designId, imageBytes);

        // Act
        byte[] result = service.getImage(designId);

        // Assert
        assertArrayEquals(imageBytes, result);
    }

    @Test
    void throwsExceptionWhenImageDoesNotExist() {
        // Arrange
        CustomDesignImageStorageService service =
                new CustomDesignImageStorageService(tempDir.toString());

        // Act & Assert
        assertThrows(
                EntityNotFoundException.class,
                () -> service.getImage(999L)
        );
    }

    @Test
    void createsStorageDirectoryIfItDoesNotExist() {
        // Arrange
        Path storageDir = tempDir.resolve("new-directory");

        CustomDesignImageStorageService service =
                new CustomDesignImageStorageService(storageDir.toString());

        byte[] imageBytes = {1, 2, 3};

        // Act
        service.saveImage(3L, imageBytes);

        // Assert
        assertTrue(Files.isDirectory(storageDir));
        assertTrue(Files.exists(storageDir.resolve("3.png")));
    }
}
