package vn.yain.controller.graphql;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import vn.yain.entity.Category;
import vn.yain.entity.Product;
import vn.yain.model.ProductInput;
import vn.yain.model.ProductPage;
import vn.yain.service.ICategoryService;
import vn.yain.service.IProductService;

@Controller
public class ProductGraphQLController {

    @Autowired
    private IProductService productService;

    @Autowired
    private ICategoryService categoryService;

    @QueryMapping
    public List<Product> allProductsOrderByPriceAsc() {
        return productService.findAllByOrderByUnitPriceAsc();
    }

    @QueryMapping
    public List<Product> productsByCategory(@Argument Long categoryId) {
        return productService.findByCategory_CategoryId(categoryId);
    }

    @QueryMapping
    public Product productById(@Argument Long productId) {
        return productService.findById(productId).orElse(null);
    }

    @QueryMapping
    public ProductPage searchProducts(@Argument String name, @Argument Long categoryId, @Argument Integer page, @Argument Integer size) {
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 6;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("productId").descending());
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

        return new ProductPage(
            productPage.getContent(),
            productPage.getTotalPages(),
            productPage.getTotalElements(),
            productPage.getNumber(),
            productPage.getSize()
        );
    }

    @SchemaMapping(typeName = "Product", field = "createDate")
    public String getCreateDate(Product product) {
        if (product.getCreateDate() == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(product.getCreateDate());
    }

    @MutationMapping
    public Product createProduct(@Argument ProductInput input) {
        Product product = new Product();
        product.setProductName(input.getProductName());
        product.setQuantity(input.getQuantity() != null ? input.getQuantity() : 0);
        product.setUnitPrice(input.getUnitPrice() != null ? input.getUnitPrice() : 0.0);
        product.setImages(input.getImages());
        product.setDescription(input.getDescription() != null ? input.getDescription() : "");
        product.setDiscount(input.getDiscount() != null ? input.getDiscount() : 0.0);
        product.setStatus(input.getStatus() != null ? input.getStatus() : 1);
        product.setCreateDate(new Date());

        if (input.getCategoryId() != null) {
            Optional<Category> optCategory = categoryService.findById(input.getCategoryId());
            optCategory.ifPresent(product::setCategory);
        }

        return productService.save(product);
    }

    @MutationMapping
    public Product updateProduct(@Argument Long productId, @Argument ProductInput input) {
        Optional<Product> opt = productService.findById(productId);
        if (opt.isPresent()) {
            Product product = opt.get();
            product.setProductName(input.getProductName());
            if (input.getQuantity() != null) {
                product.setQuantity(input.getQuantity());
            }
            if (input.getUnitPrice() != null) {
                product.setUnitPrice(input.getUnitPrice());
            }
            if (input.getImages() != null && !input.getImages().trim().isEmpty()) {
                product.setImages(input.getImages());
            }
            if (input.getDescription() != null) {
                product.setDescription(input.getDescription());
            }
            if (input.getDiscount() != null) {
                product.setDiscount(input.getDiscount());
            }
            if (input.getStatus() != null) {
                product.setStatus(input.getStatus());
            }
            if (input.getCategoryId() != null) {
                Optional<Category> optCategory = categoryService.findById(input.getCategoryId());
                optCategory.ifPresent(product::setCategory);
            }
            return productService.save(product);
        }
        return null;
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument Long productId) {
        Optional<Product> opt = productService.findById(productId);
        if (opt.isPresent()) {
            productService.delete(opt.get());
            return true;
        }
        return false;
    }
}
