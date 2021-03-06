package net.unit8.example.invariant.share;

import lombok.Value;

@Value
public class DeliveredOrderEvent {
    OrderId orderId;
    IAddress address;
    DeliveryTime deliveryTime;
}
