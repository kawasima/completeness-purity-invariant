package net.unit8.example.invariant.share;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.Validated;
import am.ik.yavi.core.Validator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryTime {
    private static final EnumSet<DayOfWeek> WEEKDAYS = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    private static final Validator<DeliveryTime> validator = ValidatorBuilder.<DeliveryTime>of()
            .build();
    LocalDateTime value;

    public static DeliveryTime of(LocalDateTime value) {
        return new DeliveryTime(value);
    }

    public static Validated<DeliveryTime> of(String value) {
        LocalDateTime dt = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
        return validator.applicative().validate(new DeliveryTime(dt));
    }

    public boolean isHoliday() {
        return WEEKDAYS.contains(value.getDayOfWeek());
    }

}
