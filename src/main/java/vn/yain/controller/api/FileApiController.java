package vn.yain.controller.api;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.yain.service.IStorageService;

@RestController
@CrossOrigin(origins = "*")
public class FileApiController {

    @Autowired
    private IStorageService storageService;

    @PostMapping("/api/files/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("");
        }
        String filename = storageService.getSorageFilename(file, UUID.randomUUID().toString());
        storageService.store(file, filename);
        return ResponseEntity.ok(filename);
    }

    @GetMapping({"/api/files/{filename:.+}", "/admin/categories/images/{filename:.+}", "/admin/products/images/{filename:.+}", "/uploads/{filename:.+}"})
    public ResponseEntity<Resource> getFile(@PathVariable("filename") String filename) {
        Resource file = storageService.loadAsResource(filename);
        String contentType = "application/octet-stream";
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) {
            contentType = MediaType.IMAGE_PNG_VALUE;
        } else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            contentType = MediaType.IMAGE_JPEG_VALUE;
        } else if (lower.endsWith(".gif")) {
            contentType = MediaType.IMAGE_GIF_VALUE;
        } else if (lower.endsWith(".webp")) {
            contentType = "image/webp";
        } else if (lower.endsWith(".svg")) {
            contentType = "image/svg+xml";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
