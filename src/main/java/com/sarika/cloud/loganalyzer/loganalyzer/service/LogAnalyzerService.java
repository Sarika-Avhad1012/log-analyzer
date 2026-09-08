package com.sarika.cloud.loganalyzer.loganalyzer.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class LogAnalyzerService {

    public Map<String, Object> analyze(String logs) {
        int errorCount = 0, warnCount = 0, infoCount = 0, debugCount = 0, traceCount = 0, fatalCount = 0;

        if (logs == null || logs.isBlank()) {
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

        // ✅ Update happens here
        String[] lines = logs.split("\\r?\\n");
        for (String line : lines) {
            String upperLine = line.toUpperCase(); // normalize

            if (upperLine.contains("ERROR")) errorCount++;
            if (upperLine.contains("WARN")) warnCount++;
            if (upperLine.contains("INFO")) infoCount++;
            if (upperLine.contains("DEBUG")) debugCount++;
            if (upperLine.contains("TRACE")) traceCount++;
            if (upperLine.contains("FATAL")) fatalCount++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalLines", lines.length);
        result.put("errorCount", errorCount);
        result.put("warnCount", warnCount);
        result.put("infoCount", infoCount);
        result.put("debugCount", debugCount);
        result.put("traceCount", traceCount);
        result.put("fatalCount", fatalCount);

        return result;
    }
}
