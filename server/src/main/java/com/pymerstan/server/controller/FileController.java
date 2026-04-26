package com.pymerstan.server.controller;

import com.pymerstan.server.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "Files", description = "Endpoints for retrieving uploaded media and documents")
public class FileController {

    private final FileStorageService fileStorageService;

    @Operation(summary = "Get Profile Photo", description = "Streams the image file for a user's profile photo")
    @GetMapping("/photos/{fileName:.+}")
    public ResponseEntity<Resource> getPhoto(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = fileStorageService.loadPhotoAsResource(fileName);
        return serveResource(resource, request);
    }

    @Operation(summary = "Get User CV", description = "Streams the PDF document for a user's CV")
    @GetMapping("/cvs/{fileName:.+}")
    public ResponseEntity<Resource> getCv(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = fileStorageService.loadCvAsResource(fileName);
        return serveResource(resource, request);
    }

    // Helper method to determine file type and return the proper response
    private ResponseEntity<Resource> serveResource(Resource resource, HttpServletRequest request) {
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Default to generic binary if type can't be determined
            contentType = "application/octet-stream";
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                // Use "inline" to display in browser, or "attachment" to force download
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}