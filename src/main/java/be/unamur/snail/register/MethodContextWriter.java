package be.unamur.snail.register;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MethodContextWriter {
    private final String csvFilePath;
    private final Map<String, Set<String>> existingEntries;

    public MethodContextWriter(String csvFilePath) throws IOException {
        this.csvFilePath = csvFilePath;
        this.existingEntries = new HashMap<>();
        loadExistingEntries();
    }

    public void loadExistingEntries() throws IOException {
        File file = new File(csvFilePath);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirst = true;
            while ((line = br.readLine()) != null) {

            }
        }
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
