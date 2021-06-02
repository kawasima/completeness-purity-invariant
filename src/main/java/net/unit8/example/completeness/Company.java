package net.unit8.example.completeness;

import lombok.Getter;

public class Company {
    @Getter
    private final String corporateDomain;

    public Company(String domain) {
        this.corporateDomain = domain;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isEmailCorporate(String email) {
        if (email == null) return false;
        return email.endsWith(corporateDomain);
    }
}
