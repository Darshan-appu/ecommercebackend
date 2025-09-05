package com.aeromatx.back.dto.vendor;

import lombok.Data;

@Data
public class VendorLoginRequest {
    private String email;
    private String password;
}
