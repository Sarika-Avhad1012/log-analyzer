package com.sarika.cloud.loganalyzer.loganalyzer.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogController.class)
class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAnalyzeLogsWithAllLevels() throws Exception {
        String logs = String.join("\n",
                "ERROR at 12:01",
                "WARN at 12:02",
                "INFO at 12:03",
                "DEBUG at 12:04",
                "INFO at 12:05"
        );

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

}
