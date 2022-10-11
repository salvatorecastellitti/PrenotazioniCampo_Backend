package com.prenotazionicampo_backend.util;

import java.io.*;
import java.nio.file.*;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
public class FileUploadUtil {

    public static void saveFile(String uploadDir, String fileName,
                                MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            log.warn(fileName);
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {

            throw new IOException("Could not save image file: " + fileName, ioe);
        }
    }
}