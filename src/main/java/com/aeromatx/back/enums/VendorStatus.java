// src/main/java/com/aeromatx/back/enums/VendorStatus.java
package com.aeromatx.back.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VendorStatus {
    PENDING,
    APPROVED,
    REJECTED;

    @JsonCreator
    public static VendorStatus fromValue(String value) {
        if (value == null) return null;
        return VendorStatus.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
