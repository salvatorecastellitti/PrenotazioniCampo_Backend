package com.prenotazionicampo_backend.util;

import java.io.*;
import java.nio.file.*;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
public class FileUploadUtil {

    public static void saveFile(String uploadDir, String fileName,
                                byte[] multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = new ByteArrayInputStream(multipartFile)) {
            log.warn(fileName);
            log.warn(inputStream);
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            log.error(ioe);
            throw new IOException("Impossibile salvare la foto: " + fileName, ioe);
        }
    }
}