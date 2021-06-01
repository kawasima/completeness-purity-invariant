package net.unit8.example.invariant.tryexecute;

import am.ik.yavi.core.ConstraintViolation;
import am.ik.yavi.core.Validated;
import am.ik.yavi.fn.Validations;
import io.fries.result.Result;
import net.unit8.example.invariant.share.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DeliverOrderHandlerImpl implements DeliverOrderHandler {
    private final LoadOrderPort loadOrderPort;
    private final SaveOrderPort saveOrderPort;

    public DeliverOrderHandlerImpl(LoadOrderPort loadOrderPort, SaveOrderPort saveOrderPort) {
        this.loadOrderPort = loadOrderPort;
        this.saveOrderPort = saveOrderPort;
    }

    @Override
    public Result<DeliveredOrderEvent> handle(DeliverOrderCommand command) {
        OrderId orderId = new OrderId(command.getOrderId());
        final Order order = loadOrderPort.load(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        Validated<Address> addressValidated = Address.of(command.getCountry(), command.getPostalCode(), command.getRegion(), command.getLocality(), command.getStreetAddress());
        Validated<DeliveryTime> deliveryTimeValidated = DeliveryTime.of(command.getDeliveryTime());

        // 注文を配送状態にし、永続化する。
        // deliverの中で、ビジネスルールがチェックされる。
        Optional<List<ConstraintViolation>> violations = Validations.combine(addressValidated, deliveryTimeValidated).apply(order::deliver)
                .fold(Optional::of,
                        validated -> validated.isValid() ? Optional.empty() : Optional.of(validated.errors()));

        if (violations.isPresent()) {
            return Result.error(new OrderDeliveryException(violations.get()));
        }
        saveOrderPort.save(order);

        return Result.ok(new DeliveredOrderEvent(
                order.getOrderId(),
                order.getDeliverAddress(),
                order.getDeliveryTime()
        ));
    }
}
