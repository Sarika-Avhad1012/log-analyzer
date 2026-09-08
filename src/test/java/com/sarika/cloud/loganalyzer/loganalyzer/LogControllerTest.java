package com.sarika.cloud.loganalyzer.loganalyzer.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import com.sarika.cloud.loganalyzer.loganalyzer.service.LogAnalyzerService;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.Map;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import java.io.IOException;
import org.springframework.mock.web.MockMultipartFile;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogController.class)
class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LogAnalyzerService analyzerService;

    @Test
    void testAnalyzeLogsWithAllLevels() throws Exception {
        String logs = String.join("\n",
                "ERROR at 12:01",
                "WARN at 12:02",
                "INFO at 12:03",
                "DEBUG at 12:04",
                "INFO at 12:05"
        );
        when(analyzerService.analyze(logs)).thenReturn(Map.of(
                "totalLines", 5,
                "errorCount", 1,
                "warnCount", 1,
                "infoCount", 2,
                "debugCount", 1,
                "traceCount", 0,
                "fatalCount", 0
        ));
        mockMvc.perform(post("/logs/analyze")
                        .contentType("text/plain")
                        .content(logs))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLines").value(5))
                .andExpect(jsonPath("$.errorCount").value(1))
                .andExpect(jsonPath("$.warnCount").value(1))
                .andExpect(jsonPath("$.infoCount").value(2))
                .andExpect(jsonPath("$.debugCount").value(1));
    }
    @Test
    void testAnalyzeLogsWithEmptyInput() throws Exception {
        String logs = ""; // empty input

        when(analyzerService.analyze(any())).thenReturn(Map.of(
                "totalLines", 0,
                "errorCount", 0,
                "warnCount", 0,
                "infoCount", 0,
                "debugCount", 0,
                "traceCount", 0,
                "fatalCount", 0
        ));

        mockMvc.perform(post("/logs/analyze")
                        .contentType("text/plain")
                        .content(logs))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLines").value(0))
                .andExpect(jsonPath("$.errorCount").value(0))
                .andExpect(jsonPath("$.warnCount").value(0))
                .andExpect(jsonPath("$.infoCount").value(0))
                .andExpect(jsonPath("$.debugCount").value(0));
    }

    @Test
    void testAnalyzeLogsWithOnlyWarn() throws Exception {
        String logs = String.join("\n",
                "WARN at 12:01",
                "WARN at 12:02",
                "WARN at 12:03"
        );
        when(analyzerService.analyze(logs)).thenReturn(Map.of(
                "totalLines", 3,
                "errorCount", 0,
                "warnCount", 3,
                "infoCount", 0,
                "debugCount", 0,
                "traceCount", 0,
                "fatalCount", 0
        ));
        mockMvc.perform(post("/logs/analyze")
                        .contentType("text/plain")
                        .content(logs))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLines").value(3))
                .andExpect(jsonPath("$.errorCount").value(0))
                .andExpect(jsonPath("$.warnCount").value(3))
                .andExpect(jsonPath("$.infoCount").value(0))
                .andExpect(jsonPath("$.debugCount").value(0));
    }
    @Test
    void testAnalyzeLogsWithTraceAndFatal() throws Exception {
        String logs = String.join("\n",
                "TRACE at 12:10",
                "FATAL at 12:11"
        );

        when(analyzerService.analyze(logs)).thenReturn(Map.of(
                "totalLines", 2,
                "errorCount", 0,
                "warnCount", 0,
                "infoCount", 0,
                "debugCount", 0,
                "traceCount", 1,
                "fatalCount", 1
        ));

        mockMvc.perform(post("/logs/analyze")
                        .contentType("text/plain")
                        .content(logs))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLines").value(2))
                .andExpect(jsonPath("$.traceCount").value(1))
                .andExpect(jsonPath("$.fatalCount").value(1));
    }
    @Test
    void testAnalyzeLogsCaseInsensitive() throws Exception {
        String logs = String.join("\n",
                "error at 12:01",
                "Warn at 12:02",
                "Info at 12:03",
                "debug at 12:04",
                "trace at 12:05",
                "fatal at 12:06"
        );

        when(analyzerService.analyze(any())).thenReturn(Map.of(
                "totalLines", 6,
                "errorCount", 1,
                "warnCount", 1,
                "infoCount", 1,
                "debugCount", 1,
                "traceCount", 1,
                "fatalCount", 1
        ));

        mockMvc.perform(post("/logs/analyze")
                        .contentType("text/plain")
                        .content(logs))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCount").value(1))
                .andExpect(jsonPath("$.warnCount").value(1))
                .andExpect(jsonPath("$.infoCount").value(1))
                .andExpect(jsonPath("$.debugCount").value(1))
                .andExpect(jsonPath("$.traceCount").value(1))
                .andExpect(jsonPath("$.fatalCount").value(1));
    }
    @Test
    void testAnalyzeLogsWithLargeInput() throws Exception {
        // Generate a large log string with 1000 lines
        StringBuilder logsBuilder = new StringBuilder();
        for (int i = 0; i < 250; i++) {
            logsBuilder.append("ERROR at ").append(i).append("\n");
            logsBuilder.append("WARN at ").append(i).append("\n");
            logsBuilder.append("INFO at ").append(i).append("\n");
            logsBuilder.append("DEBUG at ").append(i).append("\n");
        }
        String logs = logsBuilder.toString();

        // Expected counts: 250 of each level, total 1000 lines
        when(analyzerService.analyze(any())).thenReturn(Map.of(
                "totalLines", 1000,
                "errorCount", 250,
                "warnCount", 250,
                "infoCount", 250,
                "debugCount", 250,
                "traceCount", 0,
                "fatalCount", 0
        ));

        long startTime = System.nanoTime();

        mockMvc.perform(post("/logs/analyze")
                        .contentType("text/plain")
                        .content(logs))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLines").value(1000))
                .andExpect(jsonPath("$.errorCount").value(250))
                .andExpect(jsonPath("$.warnCount").value(250))
                .andExpect(jsonPath("$.infoCount").value(250))
                .andExpect(jsonPath("$.debugCount").value(250));

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        System.out.println("Stress test execution time: " + durationMs + " ms");
    }
    @Test
    void testImportLogsFileUpload() throws Exception {
        // Prepare a fake log file
        String logContent = "ERROR at 12:01\nWARN at 12:02\nINFO at 12:03";
        MockMultipartFile file = new MockMultipartFile(
                "file", "logs.txt", "text/plain", logContent.getBytes()
        );

        // Mock service response for analyzeLines
        when(analyzerService.analyzeLines(anyList())).thenReturn(Map.of(
                "totalLines", 3,
                "errorCount", 1,
                "warnCount", 1,
                "infoCount", 1,
                "debugCount", 0,
                "traceCount", 0,
                "fatalCount", 0
        ));

        // Perform request
        mockMvc.perform(multipart("/logs/import").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLines").value(3))
                .andExpect(jsonPath("$.errorCount").value(1))
                .andExpect(jsonPath("$.warnCount").value(1))
                .andExpect(jsonPath("$.infoCount").value(1))
                .andExpect(jsonPath("$.debugCount").value(0))
                .andExpect(jsonPath("$.traceCount").value(0))
                .andExpect(jsonPath("$.fatalCount").value(0));
    }
    @Test
    void testImportLogsIOException() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "logs.txt", "text/plain", "ERROR".getBytes()
        );

        // Force IOException
        when(analyzerService.analyzeLines(anyList()))
                .thenThrow(new RuntimeException("Simulated failure"));

        mockMvc.perform(multipart("/logs/import").file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to read file"));
    }

}
