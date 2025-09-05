package com.aeromatx.back.repository;

import com.aeromatx.back.entity.ProductRequestSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ProductRequestSubmissionRepository extends JpaRepository<ProductRequestSubmission, Long> {
    List<ProductRequestSubmission> findByVendorEmail(String vendorEmail);

}
