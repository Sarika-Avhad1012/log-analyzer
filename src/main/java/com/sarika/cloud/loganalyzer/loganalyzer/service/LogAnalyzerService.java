package com.sarika.cloud.loganalyzer.loganalyzer.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class LogAnalyzerService {

    // For raw text input (used by /logs/analyze)
    public Map<String, Object> analyze(String logs) {
        if (logs == null || logs.isBlank()) {
            return emptyResult();
        }
        List<String> lines = Arrays.asList(logs.split("\\r?\\n"));
        return analyzeLines(lines); // delegate to list version
    }

    // For file uploads (used by /logs/import)
    public Map<String, Object> analyzeLines(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return emptyResult();
        }

        int errorCount = 0, warnCount = 0, infoCount = 0, debugCount = 0, traceCount = 0, fatalCount = 0;

        for (String line : lines) {
            String upperLine = line.toUpperCase();
            if (upperLine.contains("ERROR")) errorCount++;
            if (upperLine.contains("WARN")) warnCount++;
            if (upperLine.contains("INFO")) infoCount++;
            if (upperLine.contains("DEBUG")) debugCount++;
            if (upperLine.contains("TRACE")) traceCount++;
            if (upperLine.contains("FATAL")) fatalCount++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalLines", lines.size());
        result.put("errorCount", errorCount);
        result.put("warnCount", warnCount);
        result.put("infoCount", infoCount);
        result.put("debugCount", debugCount);
        result.put("traceCount", traceCount);
        result.put("fatalCount", fatalCount);

        return result;
    }

    private Map<String, Object> emptyResult() {
        return Map.of(
                "totalLines", 0,
                "errorCount", 0,
                "warnCount", 0,
                "infoCount", 0,
                "debugCount", 0,
                "traceCount", 0,
                "fatalCount", 0
        );
    }
}
