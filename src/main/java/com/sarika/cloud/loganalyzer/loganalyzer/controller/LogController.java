package com.sarika.cloud.loganalyzer.loganalyzer.controller;


import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/logs")
public class LogController {

    @PostMapping("/analyze")
    public Map<String, Object> analyzeLogs(@RequestBody(required = false) String logs) {
        int errorCount = 0;
        int warnCount = 0;
        int infoCount = 0;
        int debugCount = 0;

        // Handle null or blank input
        if (logs == null || logs.isBlank()) {
            Map<String, Object> result = new HashMap<>();
            result.put("totalLines", 0);
            result.put("errorCount", 0);
            result.put("warnCount", 0);
            result.put("infoCount", 0);
            result.put("debugCount", 0);
            return result;
        }

        String[] lines = logs.split("\\r?\\n"); // handles both \n and \r\n
        for (String line : lines) {
            if (line.contains("ERROR")) errorCount++;
            if (line.contains("WARN")) warnCount++;
            if (line.contains("INFO")) infoCount++;
            if (line.contains("DEBUG")) debugCount++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalLines", lines.length);
        result.put("errorCount", errorCount);
        result.put("warnCount", warnCount);
        result.put("infoCount", infoCount);
        result.put("debugCount", debugCount);

        return result;
    }

}
