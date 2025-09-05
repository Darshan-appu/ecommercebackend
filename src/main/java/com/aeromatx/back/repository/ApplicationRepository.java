

package com.aeromatx.back.repository;

import com.aeromatx.back.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    // In your ApplicationRepository interface
@Query("SELECT a FROM Application a JOIN FETCH a.products p JOIN FETCH p.vendor")
List<Application> findAllWithProductsAndVendors();
}
