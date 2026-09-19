package vn.yain.controller.graphql;

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
import org.springframework.stereotype.Controller;
import vn.yain.entity.Category;
import vn.yain.model.CategoryInput;
import vn.yain.model.CategoryPage;
import vn.yain.service.ICategoryService;

@Controller
public class CategoryGraphQLController {

    @Autowired
    private ICategoryService categoryService;

    @QueryMapping
    public List<Category> allCategories() {
        return categoryService.findAll();
    }

    @QueryMapping
    public Category categoryById(@Argument Long categoryId) {
        return categoryService.findById(categoryId).orElse(null);
    }

    @QueryMapping
    public CategoryPage searchCategories(@Argument String name, @Argument Integer page, @Argument Integer size) {
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 5;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("categoryId").descending());
        Page<Category> categoryPage;
        if (name != null && !name.trim().isEmpty()) {
            categoryPage = categoryService.findByCategoryNameContaining(name.trim(), pageable);
        } else {
            categoryPage = categoryService.findAll(pageable);
        }
        return new CategoryPage(
            categoryPage.getContent(),
            categoryPage.getTotalPages(),
            categoryPage.getTotalElements(),
            categoryPage.getNumber(),
            categoryPage.getSize()
        );
    }

    @MutationMapping
    public Category createCategory(@Argument CategoryInput input) {
        Category category = new Category();
        category.setCategoryName(input.getCategoryName());
        category.setIcon(input.getIcon());
        return categoryService.save(category);
    }

    @MutationMapping
    public Category updateCategory(@Argument Long categoryId, @Argument CategoryInput input) {
        Optional<Category> opt = categoryService.findById(categoryId);
        if (opt.isPresent()) {
            Category category = opt.get();
            category.setCategoryName(input.getCategoryName());
            if (input.getIcon() != null && !input.getIcon().trim().isEmpty()) {
                category.setIcon(input.getIcon());
            }
            return categoryService.save(category);
        }
        return null;
    }

    @MutationMapping
    public Boolean deleteCategory(@Argument Long categoryId) {
        Optional<Category> opt = categoryService.findById(categoryId);
        if (opt.isPresent()) {
            categoryService.delete(opt.get());
            return true;
        }
        return false;
    }
}
