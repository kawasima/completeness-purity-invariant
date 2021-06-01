package net.unit8.example.invariant.applayer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.unit8.example.invariant.share.*;

import java.util.EnumSet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Order {
    @Getter
    OrderId orderId;

    @Getter
    OrderStatus status = OrderStatus.IN_PROGRESS;

    @Getter
    EnumSet<OrderConstraint> constraints;

    @Getter
    Address deliverAddress;

    @Getter
    DeliveryTime deliveryTime;

    public static Order ofInProgress(OrderId orderId, EnumSet<OrderConstraint> constraint) {
        Order order = new Order();
        order.orderId = orderId;
        order.status = OrderStatus.IN_PROGRESS;
        order.constraints = constraint;
        return order;
    }

    public void deliver(Address address, DeliveryTime deliveryTime) {
        this.deliverAddress = address;
        this.deliveryTime = deliveryTime;
        status = OrderStatus.DELIVERING;
    }

}
