package be.unamur.snail.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StackTraceHelper {
    private final String projectPackagePrefix;
    private final StackTraceProvider stackTraceProvider;

    public StackTraceHelper(String projectPackagePrefix, StackTraceProvider stackTraceProvider) {
        this.projectPackagePrefix = projectPackagePrefix;
        this.stackTraceProvider = stackTraceProvider;
    }

    public List<StackTraceElement> getFilteredAndReversedStackTrace() {
        List<StackTraceElement> projectStackTrace = new ArrayList<>(Arrays.stream(stackTraceProvider.getStackTrace())
                .filter(element -> element.getClassName().startsWith(projectPackagePrefix))
                .toList());
        Collections.reverse(projectStackTrace);
        return projectStackTrace;
    }
}
