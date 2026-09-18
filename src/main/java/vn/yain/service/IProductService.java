package vn.yain.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.yain.entity.Product;

public interface IProductService {
    List<Product> findAll();
    Page<Product> findAll(Pageable pageable);
    Optional<Product> findById(Long id);
    Optional<Product> findByProductName(String name);
    Optional<Product> findByCreateDate(Date createAt);
    List<Product> findByProductNameContaining(String name);
    Page<Product> findByProductNameContaining(String name, Pageable pageable);
    List<Product> findAllByOrderByUnitPriceAsc();
    List<Product> findByCategory_CategoryId(Long categoryId);
    Page<Product> findByCategory_CategoryId(Long categoryId, Pageable pageable);
    Page<Product> findByProductNameContainingAndCategory_CategoryId(String name, Long categoryId, Pageable pageable);
    
    <S extends Product> S save(S entity);
    void deleteById(Long id);
    void delete(Product entity);
    long count();
}
