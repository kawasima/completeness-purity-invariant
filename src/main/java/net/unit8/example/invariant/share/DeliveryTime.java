package net.unit8.example.invariant.share;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.ObjectValidator;
import am.ik.yavi.builder.ObjectValidatorBuilder;
import lombok.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;

@Value
public class DeliveryTime {
    static Clock clock = Clock.fixed(LocalDate.of(2021,1,1).atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

    private static final EnumSet<DayOfWeek> WEEKDAYS = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    private static final ObjectValidator<LocalDateTime, LocalDateTime> valueValidator = ObjectValidatorBuilder.<LocalDateTime>of("value",
            c -> c.predicate(dt -> dt.isAfter(LocalDateTime.now(clock)), "", ""))
            .build();

    private static final Arguments1Validator<LocalDateTime, DeliveryTime> validator = valueValidator
            .andThen(DeliveryTime::new);

    LocalDateTime value;

    public static Arguments1Validator<LocalDateTime, DeliveryTime> validator() {
        return validator;
    }

    public static DeliveryTime of(LocalDateTime value) {
        return validator.validated(value);
    }

    public static DeliveryTime of(String value) {
        LocalDateTime dt = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
        return DeliveryTime.of(dt);
    }

    public boolean isHoliday() {
        return WEEKDAYS.contains(value.getDayOfWeek());
    }

}
