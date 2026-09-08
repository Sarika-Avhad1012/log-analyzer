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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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


}
