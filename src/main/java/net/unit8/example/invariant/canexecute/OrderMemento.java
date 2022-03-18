package net.unit8.example.invariant.canexecute;

import lombok.Value;
import net.unit8.example.invariant.share.Address;
import net.unit8.example.invariant.share.DeliveryTime;
import net.unit8.example.invariant.share.OrderStatus;

@Value
public class OrderMemento {
    OrderStatus orderStatus;
    Address address;
    DeliveryTime deliveryTime;
}
