package net.unit8.example.completeness;

import io.fries.result.Result;
import net.unit8.example.completeness.ChangeUserUniqueEmailByPortHandler.ChangeUserEmailCommand;
import net.unit8.example.completeness.ChangeUserUniqueEmailByPortHandler.ChangedUserEmailEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ChangeUserUniqueEmailByPortHandlerTest {
    ChangeUserUniqueEmailByPortHandler sut;

    @BeforeEach
    void setup() {
        LoadUserPort loadUserPort = mock(LoadUserPort.class);
        when(loadUserPort.load(anyInt())).thenReturn(Optional.of(new User(
                new Company("example.com"),
                "foo@example.com"
        )));
        SaveUserPort saveUserPort = mock(SaveUserPort.class);
        ExistsEmailPort existsEmailPort = mock(ExistsEmailPort.class);
        when(existsEmailPort.exists("bar@example.com")).thenReturn(true);
        when(existsEmailPort.exists("baz@example.com")).thenReturn(false);

        sut = new ChangeUserUniqueEmailByPortHandler(loadUserPort, existsEmailPort, saveUserPort);
    }

    @Test
    void conflictEmail() {
        Result<ChangedUserEmailEvent> result = sut.changeEmail(new ChangeUserEmailCommand(1, "bar@example.com"));
        assertThat(result.isError()).isTrue();
        assertThat(result.getError()).isInstanceOf(EmailAlreadyTakenException.class);
    }

    @Test
    void success() {
        Result<ChangedUserEmailEvent> result = sut.changeEmail(new ChangeUserEmailCommand(1, "baz@example.com"));
        assertThat(result.isOk()).as("重複しないアドレスなので成功する").isTrue();
    }

}