package net.unit8.example.completeness;

import io.fries.result.Result;
import lombok.Value;

public class ChangeUserUniqueEmailHandler {
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
    private final ExistsEmailPort existsEmailPort;
    private final SaveUserPort saveUserPort;

    public ChangeUserUniqueEmailHandler(LoadUserPort loadUserPort, ExistsEmailPort existsEmailPort, SaveUserPort saveUserPort) {
        this.loadUserPort = loadUserPort;
        this.existsEmailPort = existsEmailPort;
        this.saveUserPort = saveUserPort;
    }


    public Result<ChangeUserEmailHandler.ChangedUserEmailEvent> changeEmail(ChangeUserEmailHandler.ChangeUserEmailCommand command) {
        if (existsEmailPort.exists(command.getNewEmail())) {
            return Result.error(new EmailAlreadyTakenException(command.getNewEmail()));
        }
        User user = loadUserPort.load(command.getUserId()).orElseThrow();
        String oldEmail = user.getEmail();

        Result<User> result = user.changeEmail(command.getNewEmail());
        if (result.isError()) {
            return Result.error(result.getError());
        }
        saveUserPort.save(user);

        return Result.ok(new ChangeUserEmailHandler.ChangedUserEmailEvent(
                command.getUserId(),
                oldEmail,
                command.getNewEmail()
        ));
    }

}
