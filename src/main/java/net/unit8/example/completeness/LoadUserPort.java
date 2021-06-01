package net.unit8.example.completeness;

import java.util.Optional;

public interface LoadUserPort {
    Optional<User> load(int userId);
}
