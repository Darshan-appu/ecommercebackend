// src/main/java/com/aeromatx/back/repository/VendorRepository.java
package com.aeromatx.back.repository;

import com.aeromatx.back.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> { // CHANGED from <Vendor, String> to <Vendor, Long>
    boolean existsByEmail(String email);
    Optional<Vendor> findByEmail(String email);

    //Optional<Vendor> findByEmail(String email);
Optional<Vendor> findByPhoneDay(String phoneDay);
Optional<Vendor> findByPhoneEvening(String phoneEvening);


}
