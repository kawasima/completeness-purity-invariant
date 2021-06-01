package net.unit8.example.invariant.share;

import io.fries.result.Result;

public interface DeliverOrderHandler {
    Result<DeliveredOrderEvent> handle(DeliverOrderCommand command);
}
