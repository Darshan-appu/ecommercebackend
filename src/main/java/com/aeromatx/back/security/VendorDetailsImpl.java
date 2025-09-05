package com.aeromatx.back.security;

import com.aeromatx.back.entity.Vendor;
import com.aeromatx.back.enums.VendorStatus;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@AllArgsConstructor
public class VendorDetailsImpl implements UserDetails {

    private final Vendor vendor;

    public static VendorDetailsImpl build(Vendor vendor) {
        return new VendorDetailsImpl(vendor);
    }

    // @Override
    // public Collection<? extends GrantedAuthority> getAuthorities() {
    //     return Collections.emptyList(); // or use roles if needed
    // }

    @Override
    public String getPassword() {
        return vendor.getPassword();
    }

    @Override
    public String getUsername() {
        return vendor.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
public boolean isEnabled() {
    return vendor.getStatus() == VendorStatus.APPROVED;

    
}

@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(() -> "ROLE_OEM");
}


}
