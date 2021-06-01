package net.unit8.example.completeness;

public class EmailAlreadyTakenException extends RuntimeException{
    public EmailAlreadyTakenException(String newEmail) {
        super(newEmail);
    }
}
