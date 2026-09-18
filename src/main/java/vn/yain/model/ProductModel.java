package vn.yain.model;

import org.springframework.web.multipart.MultipartFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private Double discount;
    private String description;
    private Long categoryId;
    private Short status;
    private MultipartFile imageFile;
}
