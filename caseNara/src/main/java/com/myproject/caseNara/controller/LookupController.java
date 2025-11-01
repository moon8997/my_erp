package com.myproject.caseNara.controller;

import com.myproject.caseNara.service.LookupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/lookup")
public class LookupController {

    private final LookupService lookupService;

    public LookupController(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    @GetMapping("/bootstrap")
    public ResponseEntity<?> bootstrap() {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "customers", lookupService.getAllCustomerNames(),
                "products", lookupService.getAllProductNames()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh() {
        lookupService.invalidateAll();
        return ResponseEntity.ok(Map.of("success", true));
    }
}