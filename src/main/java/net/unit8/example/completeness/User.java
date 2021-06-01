package net.unit8.example.completeness;

import io.fries.result.Result;
import lombok.Getter;

import java.util.List;

public class User {
    @Getter
    Company company;

    @Getter
    String email;

    public Result<User> changeEmail(String newEmail) {
        if (!company.isEmailCorporate(newEmail)) {
            return Result.error(new EmailNotCorporateException(company.getCorporateDomain()));
        }
        this.email = newEmail;
        return Result.ok(this);
    }

    public Result<User> changeEmail(String newEmail, ExistsEmailPort existsEmailPort) {
        if (existsEmailPort.exists(newEmail)) {
            return Result.error(new EmailAlreadyTakenException(newEmail));
        }
        if (!company.isEmailCorporate(newEmail)) {
            return Result.error(new EmailNotCorporateException(company.getCorporateDomain()));
        }
        this.email = newEmail;
        return Result.ok(this);
    }

    public Result<User> changeEmail(String newEmail, List<User> allUsers) {
        if (allUsers.stream().anyMatch(user -> user.getEmail().equals(newEmail))) {
            return Result.error(new EmailAlreadyTakenException(newEmail));
        }

        if (!company.isEmailCorporate(newEmail)) {
            return Result.error(new EmailNotCorporateException(company.getCorporateDomain()));
        }
        this.email = newEmail;
        return Result.ok(this);
    }
}
