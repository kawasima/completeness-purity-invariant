package net.unit8.example.invariant.incompleteness;

import lombok.Value;
import net.unit8.example.invariant.incompleteness.address.ExistingAddress;
import net.unit8.example.invariant.share.DeliveryTime;
import net.unit8.example.invariant.share.OrderStatus;

@Value
public class OrderMemento {
    OrderStatus orderStatus;
    ExistingAddress address;
    DeliveryTime deliveryTime;
}
