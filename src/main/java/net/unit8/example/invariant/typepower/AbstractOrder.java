package net.unit8.example.invariant.typepower;

import lombok.Getter;
import net.unit8.example.invariant.share.OrderConstraint;
import net.unit8.example.invariant.share.OrderId;

import java.util.EnumSet;

public abstract class AbstractOrder implements Order{
    @Getter
    OrderId orderId;
    @Getter
    EnumSet<OrderConstraint> constraints;
}
