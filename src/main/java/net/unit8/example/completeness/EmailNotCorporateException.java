package net.unit8.example.completeness;

public class EmailNotCorporateException extends RuntimeException {
    public EmailNotCorporateException(String corporateDomain) {
        super(corporateDomain);
    }
}
