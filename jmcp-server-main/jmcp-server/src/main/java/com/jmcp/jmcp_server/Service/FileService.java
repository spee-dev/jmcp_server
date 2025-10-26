package com.jmcp.jmcp_server.Service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {

    public void createFile(String path) throws IOException {
        File file = new File(path);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        file.createNewFile();
    }

    public void createFolder(String path) {
        new File(path).mkdir();
    }

    public List<String> searchFile(String fileName, String startDir) throws IOException {
        return Files.walk(Paths.get(startDir))
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().equals(fileName))
                .map(path -> path.toAbsolutePath().toString())
                .collect(Collectors.toList());
    }

    public List<String> listFiles(String directoryPath) {
        return Arrays.stream(new File(directoryPath).listFiles())
                .map(File::getName)
                .collect(Collectors.toList());
    }

    public void deleteFile(String path) {
        new File(path).delete();
    }

    public void openFile(String path) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb;

        if (os.contains("win")) {
            pb = new ProcessBuilder("cmd", "/c", "start", path);
        } else if (os.contains("mac")) {
            pb = new ProcessBuilder("open", path);
        } else {
            pb = new ProcessBuilder("xdg-open", path);
        }
        pb.start();
    }
}
