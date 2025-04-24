package be.unamur.snail.register;

import java.io.*;
import java.util.*;

public class MethodContextWriter {
    private final String csvFilePath;
    private final Map<String, Set<String>> existingEntries;

    public MethodContextWriter(String csvFilePath) {
        this.csvFilePath = csvFilePath;
        this.existingEntries = new HashMap<>();
    }

    public String[] splitCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        tokens.add(current.toString());
        return tokens.toArray(new String[0]);
    }

    public synchronized void writeIfNotExists(MethodContext methodContext) throws IOException {
        String key = methodContext.getClassName() + "|" + methodContext.getFileName();
        String csvRow = methodContext.toCsvRow();

        existingEntries.putIfAbsent(key, new HashSet<>());
        if (!existingEntries.get(key).contains(csvRow)) {
            try (PrintWriter out = new PrintWriter(new FileWriter(csvFilePath, true))) {
                out.println(csvRow);
            }
            existingEntries.get(key).add(csvRow);
        }
    }
}
