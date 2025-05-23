package be.unamur.snail.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StackTraceHelper {
    private final String projectPackagePrefix;
    private final StackTraceProvider stackTraceProvider;

    public StackTraceHelper(String projectPackagePrefix, StackTraceProvider stackTraceProvider) {
        this.projectPackagePrefix = projectPackagePrefix;
        this.stackTraceProvider = stackTraceProvider;
    }

    public List<StackTraceElement> getFilteredStackTrace() {
        return new ArrayList<>(Arrays.stream(this.stackTraceProvider.getStackTrace())
                .filter(element -> element.getClassName().startsWith(this.projectPackagePrefix))
                .toList());
    }
}
