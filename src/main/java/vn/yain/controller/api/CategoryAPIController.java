package vn.yain.controller.api;

import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.yain.entity.Category;
import vn.yain.model.Response;
import vn.yain.service.ICategoryService;
import vn.yain.service.IStorageService;

@RestController
@RequestMapping(path = "/api/category")
@CrossOrigin(origins = "*")
public class CategoryAPIController {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IStorageService storageService;

    @GetMapping
    public ResponseEntity<Response> getAllCategory() {
        return new ResponseEntity<>(new Response(true, "Thành công", categoryService.findAll()), HttpStatus.OK);
    }

    @GetMapping(path = "/page")
    public ResponseEntity<Response> getCategoryPage(
            @RequestParam(name = "name", defaultValue = "") String name,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("categoryId").descending());
        Page<Category> categoryPage;
        if (name != null && !name.trim().isEmpty()) {
            categoryPage = categoryService.findByCategoryNameContaining(name.trim(), pageable);
        } else {
            categoryPage = categoryService.findAll(pageable);
        }
        return new ResponseEntity<>(new Response(true, "Thành công", categoryPage), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Response> getCategoryById(@PathVariable("id") Long id) {
        Optional<Category> category = categoryService.findById(id);
        if (category.isPresent()) {
            return new ResponseEntity<>(new Response(true, "Thành công", category.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Response(false, "Không tìm thấy Category", null), HttpStatus.NOT_FOUND);
    }

    @PostMapping(path = "/getCategory")
    public ResponseEntity<Response> getCategory(@Validated @RequestParam("id") Long id) {
        Optional<Category> category = categoryService.findById(id);
        if (category.isPresent()) {
            return new ResponseEntity<>(new Response(true, "Thành công", category.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Response(false, "Thất bại", null), HttpStatus.NOT_FOUND);
    }

    @PostMapping(path = "/addCategory")
    public ResponseEntity<Response> addCategory(
            @Validated @RequestParam("categoryName") String categoryName,
            @RequestParam(value = "icon", required = false) MultipartFile icon) {
        Optional<Category> optCategory = categoryService.findByCategoryName(categoryName.trim());
        if (optCategory.isPresent()) {
            return new ResponseEntity<>(new Response(false, "Category đã tồn tại trong hệ thống", optCategory.get()), HttpStatus.BAD_REQUEST);
        }

        Category category = new Category();
        category.setCategoryName(categoryName.trim());

        if (icon != null && !icon.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String storeFilename = storageService.getSorageFilename(icon, uuid.toString());
            storageService.store(icon, storeFilename);
            category.setIcon(storeFilename);
        }

        categoryService.save(category);
        return new ResponseEntity<>(new Response(true, "Thêm Thành công", category), HttpStatus.OK);
    }

    @PutMapping(path = "/updateCategory")
    public ResponseEntity<Response> updateCategory(
            @Validated @RequestParam("categoryId") Long categoryId,
            @Validated @RequestParam("categoryName") String categoryName,
            @RequestParam(value = "icon", required = false) MultipartFile icon) {
        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy Category", null), HttpStatus.BAD_REQUEST);
        }

        Category category = optCategory.get();
        category.setCategoryName(categoryName.trim());

        if (icon != null && !icon.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String storeFilename = storageService.getSorageFilename(icon, uuid.toString());
            storageService.store(icon, storeFilename);
            category.setIcon(storeFilename);
        }

        categoryService.save(category);
        return new ResponseEntity<>(new Response(true, "Cập nhật Thành công", category), HttpStatus.OK);
    }

    @DeleteMapping(path = "/deleteCategory")
    public ResponseEntity<Response> deleteCategory(@Validated @RequestParam("categoryId") Long categoryId) {
        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy Category", null), HttpStatus.BAD_REQUEST);
        }

        categoryService.delete(optCategory.get());
        return new ResponseEntity<>(new Response(true, "Xóa Thành công", optCategory.get()), HttpStatus.OK);
    }
}
