package com.aeromatx.back.security;

import com.aeromatx.back.entity.Vendor;
import com.aeromatx.back.repository.VendorRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VendorDetailsServiceImpl implements UserDetailsService {

    private final VendorRepository vendorRepository;

    public VendorDetailsServiceImpl(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Vendor> vendor = vendorRepository.findByEmail(email);
        if (vendor.isEmpty()) {
            throw new UsernameNotFoundException("Vendor not found with email: " + email);
        }

        return VendorDetailsImpl.build(vendor.get());
    }
}
