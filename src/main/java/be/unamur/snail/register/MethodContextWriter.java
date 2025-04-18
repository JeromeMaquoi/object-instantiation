package be.unamur.snail.register;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MethodContextWriter {
    private final String csvFilePath;

    public MethodContextWriter(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    public void write(MethodContext methodContext) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(csvFilePath, true))) {
            out.println(methodContext.toCsvRow());
        }
    }
}
