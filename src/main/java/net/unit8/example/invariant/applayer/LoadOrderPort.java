package net.unit8.example.invariant.applayer;

import net.unit8.example.invariant.share.OrderId;

import java.util.Optional;

public interface LoadOrderPort {
    Optional<Order> load(OrderId orderId);
}
