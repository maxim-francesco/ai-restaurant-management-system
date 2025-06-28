package com.example.gallery_service.controller;

import com.example.gallery_service.dto.CreateEventCategoryRequest;
import com.example.gallery_service.dto.EventCategoryDto;
import com.example.gallery_service.service.EventCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marchează clasa ca fiind un Controler REST (returnează JSON)
@RequestMapping("/api/v1/categories") // Toate URL-urile din acest controler vor începe cu /api/v1/categories
@RequiredArgsConstructor
public class EventCategoryController {

    // Injectăm interfața serviciului, nu implementarea
    private final EventCategoryService eventCategoryService;

    // POST /api/v1/categories
    @PostMapping
    public ResponseEntity<EventCategoryDto> createCategory(@RequestBody CreateEventCategoryRequest request) {
        EventCategoryDto createdCategory = eventCategoryService.createCategory(request);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    // GET /api/v1/categories
    @GetMapping
    public ResponseEntity<List<EventCategoryDto>> getAllCategories() {
        List<EventCategoryDto> categories = eventCategoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // DELETE /api/v1/categories/{categoryId}
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        eventCategoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build(); // Returnează un status 204 No Content
    }
}