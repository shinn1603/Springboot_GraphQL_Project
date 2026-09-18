package vn.yain.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.yain.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProductNameContaining(String name);
    Page<Product> findByProductNameContaining(String name, Pageable pageable);
    Optional<Product> findByProductName(String name);
    Optional<Product> findByCreateDate(Date createAt);

    // Chức năng 1: Hiển thị tất cả product có price từ thấp đến cao
    List<Product> findAllByOrderByUnitPriceAsc();

    // Chức năng 2: Lấy tất cả product của 01 category
    List<Product> findByCategory_CategoryId(Long categoryId);

    // Chức năng 3: Phân trang theo category và tìm kiếm tên
    Page<Product> findByCategory_CategoryId(Long categoryId, Pageable pageable);
    Page<Product> findByProductNameContainingAndCategory_CategoryId(String name, Long categoryId, Pageable pageable);
}
