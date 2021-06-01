package net.unit8.example.completeness;

import io.fries.result.Result;
import lombok.Value;
import org.springframework.stereotype.Component;

/**
 * ドメインモデルの例
 */
@Component
public class ChangeUserEmailHandler {
    @Value
    public static class ChangeUserEmailCommand {
        int userId;
        String newEmail;
    }
    @Value
    public static class ChangedUserEmailEvent {
        int userId;
        String oldEmail;
        String newEmail;
    }

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;

    public ChangeUserEmailHandler(LoadUserPort loadUserPort, SaveUserPort saveUserPort) {
        this.loadUserPort = loadUserPort;
        this.saveUserPort = saveUserPort;
    }


    public Result<ChangedUserEmailEvent> changeEmail(ChangeUserEmailCommand command) {
        User user = loadUserPort.load(command.getUserId()).orElseThrow();
        String oldEmail = user.getEmail();

        Result<User> result = user.changeEmail(command.getNewEmail());
        if (result.isError()) {
            return Result.error(result.getError());
        }
        saveUserPort.save(user);

        return Result.ok(new ChangedUserEmailEvent(
                command.getUserId(),
                oldEmail,
                command.getNewEmail()
        ));
    }
}
