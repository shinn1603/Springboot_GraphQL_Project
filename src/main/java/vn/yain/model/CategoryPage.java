package vn.yain.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.yain.entity.Category;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryPage {
    private List<Category> content;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int pageSize;
}
