package com.aeromatx.back.security;

import com.aeromatx.back.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username; // ✅ Added username field
    private String email;

    @JsonIgnore
    private String password;

    private String mobile;
    private String permanentAddress;
    private boolean mainAdmin;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id,
                           String username,
                           String email,
                           String password,
                           String mobile,
                           String permanentAddress,
                           boolean mainAdmin,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
        this.permanentAddress = permanentAddress;
        this.mainAdmin = mainAdmin;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),    // ✅ Correct username set
                user.getEmail(),
                user.getPassword(),
                user.getMobile(),
                user.getPermanentAddress(),
                user.isMainAdmin(),
                authorities
        );
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username; // ✅ Correct username returned
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public boolean isMainAdmin() {
        return mainAdmin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
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
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl that = (UserDetailsImpl) o;
        return Objects.equals(id, that.id);
    }
}
