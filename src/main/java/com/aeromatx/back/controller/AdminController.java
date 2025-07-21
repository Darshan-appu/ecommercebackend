package com.aeromatx.back.controller;

import com.aeromatx.back.dto.auth.ChangePasswordRequest;
import com.aeromatx.back.dto.product.ProductDTO;
import com.aeromatx.back.entity.Product;
import com.aeromatx.back.entity.ProductSpecification;
import com.aeromatx.back.entity.SubCategory;
import com.aeromatx.back.entity.User;
import com.aeromatx.back.repository.ProductRepository;
import com.aeromatx.back.repository.SubCategoryRepository;
import com.aeromatx.back.repository.UserRepository;
import com.aeromatx.back.security.UserDetailsImpl;
import com.aeromatx.back.service.UserService;
import com.aeromatx.back.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")  // 👈 Only accessible by ADMIN role
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    // ✅ Get admin profile
    @GetMapping("/profile")
    public ResponseEntity<?> getAdminProfile(Authentication authentication) {
        UserDetailsImpl adminDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getUserById(adminDetails.getId()));
    }

    // ✅ Change password endpoint
    @PutMapping("/profile/password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ) {
        UserDetailsImpl adminDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userService.changePassword(
                adminDetails.getId(),
                request.getCurrentPassword(),
                request.getNewPassword()
        );
    }

    // ✅ Get all admins
    @GetMapping("/all")
    public ResponseEntity<?> getAllAdmins() {
        return ResponseEntity.ok(userService.getAllAdmins());
    }

    // ✅ Change main admin
    @PutMapping("/change-main/{newAdminId}")
    public ResponseEntity<?> changeMainAdmin(@PathVariable Long newAdminId, @RequestHeader("Authorization") String token) {
        String currentAdminEmail = jwtUtil.getUserNameFromJwtToken(token.substring(7));

        User currentAdmin = userRepository.findByEmail(currentAdminEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Current admin not found"));

        if (!currentAdmin.isMainAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only the Main Admin can perform this action");
        }

        User newAdmin = userRepository.findById(newAdminId)
                .orElseThrow(() -> new UsernameNotFoundException("Target admin not found"));

        currentAdmin.setMainAdmin(false);
        newAdmin.setMainAdmin(true);

        userRepository.save(currentAdmin);
        userRepository.save(newAdmin);

        return ResponseEntity.ok("Main admin role transferred successfully.");
    }

    // ✅ Add new product with specifications
    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setStatus(dto.getStatus());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());

        SubCategory subCat = subCategoryRepository.findById(dto.getSubCategoryId())
                .orElseThrow(() -> new RuntimeException("SubCategory not found"));
        product.setSubCategory(subCat);

        List<ProductSpecification> specs = dto.getSpecifications().stream()
                .map(spec -> new ProductSpecification(spec.getKey(), spec.getValue(), product))
                .collect(Collectors.toList());

        product.setSpecifications(specs);

        return ResponseEntity.ok(productRepository.save(product));
    }
}
