package net.unit8.example.invariant.typepower;

import net.unit8.example.invariant.share.OrderConstraint;
import net.unit8.example.invariant.share.OrderId;

import java.util.EnumSet;

public interface Order {
    static InProgressOrder ofInProgress(OrderId orderId, EnumSet<OrderConstraint> constraint) {
        InProgressOrder order = new InProgressOrder();
        order.orderId = orderId;
        order.constraints = constraint;
        return order;
    }

    OrderId getOrderId();
    EnumSet<OrderConstraint> getConstraints();
}
