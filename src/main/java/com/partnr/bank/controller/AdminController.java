package com.partnr.bank.controller;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final EventProcessingConfiguration eventProcessingConfiguration;

    public AdminController(EventProcessingConfiguration eventProcessingConfiguration) {
        this.eventProcessingConfiguration = eventProcessingConfiguration;
    }

    @PostMapping("/replay/{processingGroup}")
    public ResponseEntity<Map<String, String>> replayEvents(@PathVariable String processingGroup) {
        Optional<TrackingEventProcessor> optionalProcessor =
            eventProcessingConfiguration.eventProcessor(processingGroup, TrackingEventProcessor.class);

        if (optionalProcessor.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error",
                "No tracking event processor found for: " + processingGroup));
        }

        TrackingEventProcessor processor = optionalProcessor.get();
        processor.shutDown();
        processor.resetTokens();
        processor.start();

        return ResponseEntity.ok(Map.of("message",
            "Replay triggered for processing group: " + processingGroup));
    }
}
