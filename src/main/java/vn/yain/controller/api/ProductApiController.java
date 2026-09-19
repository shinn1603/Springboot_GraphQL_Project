package vn.yain.controller.api;

import java.util.Date;
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
import vn.yain.entity.Product;
import vn.yain.model.Response;
import vn.yain.service.ICategoryService;
import vn.yain.service.IProductService;
import vn.yain.service.IStorageService;

@RestController
@RequestMapping(path = "/api/product")
@CrossOrigin(origins = "*")
public class ProductApiController {

    @Autowired
    private IProductService productService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IStorageService storageService;

    @GetMapping
    public ResponseEntity<Response> getAllProduct() {
        return new ResponseEntity<>(new Response(true, "Thành công", productService.findAll()), HttpStatus.OK);
    }

    @GetMapping(path = "/page")
    public ResponseEntity<Response> getProductPage(
            @RequestParam(name = "name", defaultValue = "") String name,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").descending());
        Page<Product> productPage;

        boolean hasName = (name != null && !name.trim().isEmpty());
        boolean hasCategory = (categoryId != null && categoryId > 0);

        if (hasName && hasCategory) {
            productPage = productService.findByProductNameContainingAndCategory_CategoryId(name.trim(), categoryId, pageable);
        } else if (hasCategory) {
            productPage = productService.findByCategory_CategoryId(categoryId, pageable);
        } else if (hasName) {
            productPage = productService.findByProductNameContaining(name.trim(), pageable);
        } else {
            productPage = productService.findAll(pageable);
        }

        return new ResponseEntity<>(new Response(true, "Thành công", productPage), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Response> getProductById(@PathVariable("id") Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            return new ResponseEntity<>(new Response(true, "Thành công", product.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Response(false, "Không tìm thấy Product", null), HttpStatus.NOT_FOUND);
    }

    @PostMapping(path = "/getProduct")
    public ResponseEntity<Response> getProduct(@Validated @RequestParam("id") Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            return new ResponseEntity<>(new Response(true, "Thành công", product.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Response(false, "Thất bại", null), HttpStatus.NOT_FOUND);
    }

    @PostMapping(path = "/addProduct")
    public ResponseEntity<Response> addProduct(
            @Validated @RequestParam("productName") String productName,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @Validated @RequestParam("unitPrice") Double unitPrice,
            @Validated @RequestParam("discount") Double discount,
            @Validated @RequestParam("description") String description,
            @Validated @RequestParam("categoryId") Long categoryId,
            @Validated @RequestParam("quantity") Integer quantity,
            @Validated @RequestParam("status") Short status) {

        Optional<Product> optProduct = productService.findByProductName(productName.trim());
        if (optProduct.isPresent()) {
            return new ResponseEntity<>(new Response(false, "Sản phẩm này đã tồn tại trong hệ thống", optProduct.get()), HttpStatus.BAD_REQUEST);
        }

        Product product = new Product();
        product.setProductName(productName.trim());
        product.setUnitPrice(unitPrice);
        product.setDiscount(discount);
        product.setDescription(description);
        product.setQuantity(quantity);
        product.setStatus(status);
        product.setCreateDate(new Date());

        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isPresent()) {
            product.setCategory(optCategory.get());
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String storeFilename = storageService.getSorageFilename(imageFile, uuid.toString());
            storageService.store(imageFile, storeFilename);
            product.setImages(storeFilename);
        }

        Product saved = productService.save(product);
        return new ResponseEntity<>(new Response(true, "Thêm Thành công", saved), HttpStatus.OK);
    }

    @PutMapping(path = "/updateProduct")
    public ResponseEntity<Response> updateProduct(
            @Validated @RequestParam("productId") Long productId,
            @Validated @RequestParam("productName") String productName,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @Validated @RequestParam("unitPrice") Double unitPrice,
            @Validated @RequestParam("discount") Double discount,
            @Validated @RequestParam("description") String description,
            @Validated @RequestParam("categoryId") Long categoryId,
            @Validated @RequestParam("quantity") Integer quantity,
            @Validated @RequestParam("status") Short status) {

        Optional<Product> optProduct = productService.findById(productId);
        if (optProduct.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy Product", null), HttpStatus.BAD_REQUEST);
        }

        Product product = optProduct.get();
        product.setProductName(productName.trim());
        product.setUnitPrice(unitPrice);
        product.setDiscount(discount);
        product.setDescription(description);
        product.setQuantity(quantity);
        product.setStatus(status);

        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isPresent()) {
            product.setCategory(optCategory.get());
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String storeFilename = storageService.getSorageFilename(imageFile, uuid.toString());
            storageService.store(imageFile, storeFilename);
            product.setImages(storeFilename);
        }

        Product updated = productService.save(product);
        return new ResponseEntity<>(new Response(true, "Cập nhật Thành công", updated), HttpStatus.OK);
    }

    @DeleteMapping(path = "/deleteProduct")
    public ResponseEntity<Response> deleteProduct(@Validated @RequestParam("productId") Long productId) {
        Optional<Product> optProduct = productService.findById(productId);
        if (optProduct.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy Product", null), HttpStatus.BAD_REQUEST);
        }

        productService.delete(optProduct.get());
        return new ResponseEntity<>(new Response(true, "Xóa Thành công", optProduct.get()), HttpStatus.OK);
    }
}
