package com.sarika.cloud.loganalyzer.loganalyzer.controller;


import com.sarika.cloud.loganalyzer.loganalyzer.service.LogAnalyzerService;
import org.springframework.web.bind.annotation.*;
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
}
