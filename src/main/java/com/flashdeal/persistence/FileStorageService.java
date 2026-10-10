package com.flashdeal.persistence;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles underlying file system I/O, file creation, headers, and safe CSV reading/writing.
 * Uses try-with-resources and synchronized write operations to ensure file integrity.
 */
public class FileStorageService {
    private static final String DATA_DIR_PATH = "data";
    private final Path dataDirectory;
    private final Object fileLock = new Object();

    public FileStorageService() {
        this.dataDirectory = Paths.get(DATA_DIR_PATH);
        ensureDataDirectoryExists();
    }

    public FileStorageService(String customDirPath) {
        this.dataDirectory = Paths.get(customDirPath);
        ensureDataDirectoryExists();
    }

    private void ensureDataDirectoryExists() {
        try {
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }
        } catch (IOException e) {
            System.err.println("Failed to create data directory: " + e.getMessage());
        }
    }

    public Path getFilePath(String fileName) {
        return dataDirectory.resolve(fileName);
    }

    public boolean fileExists(String fileName) {
        return Files.exists(getFilePath(fileName));
    }

    /**
     * Reads all lines from a CSV file. Returns empty list if file doesn't exist.
     */
    public List<String> readLines(String fileName) throws IOException {
        Path path = getFilePath(fileName);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    /**
     * Overwrites a file with the given header and lines.
     */
    public void writeLines(String fileName, String header, List<String> lines) throws IOException {
        Path path = getFilePath(fileName);
        synchronized (fileLock) {
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                if (header != null && !header.isEmpty()) {
                    writer.write(header);
                    writer.newLine();
                }
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
    }

    /**
     * Appends a single line to a file. Writes the header first if file is newly created.
     */
    public void appendLine(String fileName, String header, String line) throws IOException {
        Path path = getFilePath(fileName);
        synchronized (fileLock) {
            boolean isNewFile = !Files.exists(path) || Files.size(path) == 0;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), StandardCharsets.UTF_8, true))) {
                if (isNewFile && header != null && !header.isEmpty()) {
                    writer.write(header);
                    writer.newLine();
                }
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Escapes fields containing commas or quotes for CSV standard RFC 4180.
     */
    public static String escapeCsvField(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    /**
     * Splits a CSV line taking quotes into account.
     */
    public static List<String> parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        if (line == null) return tokens;

        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    sb.append('\"');
                    i++; // skip escaped quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());
        return tokens;
    }
}
