package net.unit8.example.invariant.incompleteness;

import net.unit8.example.invariant.share.OrderId;

import java.util.Optional;

public interface LoadOrderPort {
    Optional<Order> load(OrderId orderId);
}
