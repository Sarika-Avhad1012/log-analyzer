package com.sarika.cloud.loganalyzer.loganalyzer.controller;


import com.sarika.cloud.loganalyzer.loganalyzer.service.LogAnalyzerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogAnalyzerService logAnalyzerService;

    // Constructor injection
    public LogController(LogAnalyzerService logAnalyzerService) {
        this.logAnalyzerService = logAnalyzerService;
    }

    @PostMapping("/analyze")
    public Map<String, Object> analyzeLogs(@RequestBody(required = false) String logs) {
        return logAnalyzerService.analyze(logs);
    }
    @PostMapping("/import")
    public ResponseEntity<?> importLogs(@RequestParam("file") MultipartFile file) {
        try {
            List<String> lines = new BufferedReader(
                    new InputStreamReader(file.getInputStream()))
                    .lines()
                    .toList();

            if (lines.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Uploaded file is empty"));
            }

            // Call the correct service method
            Map<String, Object> result = logAnalyzerService.analyzeLines(lines);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to read file"));
    }

}

}
