package vn.yain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInput {
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private String images;
    private String description;
    private Double discount;
    private Short status;
    private Long categoryId;
}
