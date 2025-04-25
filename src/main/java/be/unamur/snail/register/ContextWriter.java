package be.unamur.snail.register;

import java.io.*;
import java.util.*;

public class ContextWriter<T extends CsvWritableContext> {
    private final String csvFilePath;
    private final Map<String, Set<String>> existingEntries = new HashMap<>();

    public ContextWriter(String csvFilePath) {
        this.csvFilePath = csvFilePath;
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

    public synchronized void writeIfNotExists(T context) throws IOException {
        String key = context.getClassName() + "|" + context.getFileName();
        String csvRow = context.toCsvRow();

        existingEntries.putIfAbsent(key, new HashSet<>());
        if (!existingEntries.get(key).contains(csvRow)) {
            try (PrintWriter out = new PrintWriter(new FileWriter(csvFilePath, true))) {
                out.println(csvRow);
            }
            existingEntries.get(key).add(csvRow);
        }
    }
}
