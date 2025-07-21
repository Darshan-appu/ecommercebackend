package com.aeromatx.back.service;

import com.aeromatx.back.entity.Product;
import com.aeromatx.back.entity.ProductSpecification;
import com.aeromatx.back.repository.ProductRepository;
import com.aeromatx.back.repository.ProductSpecificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSpecificationService {

    @Autowired
    private ProductSpecificationRepository specRepo;

    @Autowired
    private ProductRepository productRepo;

    public ProductSpecification addSpecification(Long productId, ProductSpecification spec) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        spec.setProduct(product);
        return specRepo.save(spec);
    }

    public List<ProductSpecification> getSpecifications(Long productId) {
        return productRepo.findById(productId)
                .map(Product::getSpecifications)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void deleteSpecification(Long specId) {
        specRepo.deleteById(specId);
    }

    public ProductSpecification updateSpecification(Long specId, ProductSpecification updatedSpec) {
        ProductSpecification existing = specRepo.findById(specId)
                .orElseThrow(() -> new RuntimeException("Specification not found"));

        existing.setKey(updatedSpec.getKey());
        existing.setValue(updatedSpec.getValue());
        return specRepo.save(existing);
    }
}
