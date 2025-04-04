package Model;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileManager {
    private final Path filePath;
    private static final String DELIMITER = ",";
    private static final int METADATA_FIELDS = 5; // filename,extension,key1,key2,key3

    public FileManager() {
        this.filePath = Paths.get("src/main/java/project/Model/metadata.txt");
        initializeFile();
    }

    private void initializeFile() {
        try {
            if (!Files.exists(filePath)) {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new FileHandlerException("Could not initialize metadata file", e);
        }
    }

    public void writeMetadata(String filename, String extension, String key1, String key2, String key3) {
        validateInput(filename, extension, key1, key2, key3);

        try {
            List<String> lines = readAllLines();
            boolean updated = updateExistingEntry(lines, filename, extension, key1, key2, key3);

            if (!updated) {
                lines.add(createMetadataLine(filename, extension, key1, key2, key3));
                System.out.println("Added new file to database");
            }

            writeAllLines(lines);
        } catch (IOException e) {
            throw new FileHandlerException("Failed to write metadata", e);
        }
    }

    public Optional<Metadata> getMetadata(String filename) {
        try {
            return readAllLines().stream()
                    .filter(line -> line.startsWith(filename + DELIMITER))
                    .findFirst()
                    .map(this::parseMetadataLine);
        } catch (IOException e) {
            throw new FileHandlerException("Failed to read metadata", e);
        }
    }

    private List<String> readAllLines() throws IOException {
        if (Files.exists(filePath)) {
            return Files.readAllLines(filePath);
        }
        return new ArrayList<>();
    }

    private void writeAllLines(List<String> lines) throws IOException {
        Files.write(filePath, lines, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private boolean updateExistingEntry(List<String> lines, String filename,
                                        String extension, String key1, String key2, String key3) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith(filename + DELIMITER)) {
                lines.set(i, createMetadataLine(filename, extension, key1, key2, key3));
                System.out.println("Updated existing file in database");
                return true;
            }
        }
        return false;
    }

    private String createMetadataLine(String filename, String extension,
                                      String key1, String key2, String key3) {
        return String.join(DELIMITER, filename, extension, key1, key2, key3);
    }

    private Metadata parseMetadataLine(String line) {
        String[] parts = line.split(DELIMITER);
        if (parts.length != METADATA_FIELDS) {
            throw new FileHandlerException("Invalid metadata format");
        }
        return new Metadata(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }

    private void validateInput(String... fields) {
        if (Arrays.stream(fields).anyMatch(f -> f == null || f.contains(DELIMITER))) {
            throw new IllegalArgumentException("Invalid input parameters");
        }
    }

    public static class Metadata {
        private final String filename;
        private final String extension;
        private final String key1;
        private final String key2;
        private final String key3;

        public Metadata(String filename, String extension, String key1, String key2, String key3) {
            this.filename = filename;
            this.extension = extension;
            this.key1 = key1;
            this.key2 = key2;
            this.key3 = key3;
        }

        public String getFilename() { return filename; }
        public String getExtension() { return extension; }
        public String getKey1() { return key1; }
        public String getKey2() { return key2; }
        public String getKey3() { return key3; }
    }

    public static class FileHandlerException extends RuntimeException {
        public FileHandlerException(String message, Throwable cause) {
            super(message, cause);
        }
        public FileHandlerException(String message) {
            super(message);
        }
    }
}