package net.unit8.example.invariant.isvalid;

import am.ik.yavi.core.ConstraintViolations;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.unit8.example.invariant.share.*;

import java.util.EnumSet;

import static net.unit8.example.invariant.share.OrderConstraint.DELIVERY_WEEKDAY;
import static net.unit8.example.invariant.share.OrderConstraint.SHIPPING_JAPAN_ONLY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Order {
    @Getter
    OrderId orderId;

    @Getter
    OrderStatus status = OrderStatus.IN_PROGRESS;

    @Getter
    EnumSet<OrderConstraint> constraints;

    @Setter
    @Getter
    Address deliverAddress;

    @Setter
    @Getter
    DeliveryTime deliveryTime;

    public static Order ofInProgress(OrderId orderId, EnumSet<OrderConstraint> constraint) {
        Order order = new Order();
        order.orderId = orderId;
        order.status = OrderStatus.IN_PROGRESS;
        order.constraints = constraint;
        return order;
    }

    public ConstraintViolations validateForDelivery() {
        ConstraintViolations violations = new ConstraintViolations();
        if (constraints.contains(SHIPPING_JAPAN_ONLY) && !deliverAddress.getCountry().equals("JP")) {
            violations.add(SHIPPING_JAPAN_ONLY.getConstraintViolation());
        }

        if (constraints.contains(DELIVERY_WEEKDAY) && deliveryTime.isHoliday()) {
            violations.add(DELIVERY_WEEKDAY.getConstraintViolation());
        }
        return violations;
    }

    public void deliver() {
        status = OrderStatus.DELIVERING;
    }
}
