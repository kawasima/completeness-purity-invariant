package net.unit8.example.invariant.share;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(OrderId orderId) {
        super(orderId.getValue());
    }
}
