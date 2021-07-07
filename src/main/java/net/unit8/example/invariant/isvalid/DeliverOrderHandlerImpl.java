package net.unit8.example.invariant.isvalid;

import am.ik.yavi.core.ConstraintViolations;
import am.ik.yavi.core.Validated;
import io.fries.result.Result;
import net.unit8.example.invariant.share.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

        ConstraintViolations violations = new ConstraintViolations();
        Validated<Address> addressValidated = Address.validator().validate(command.getCountry(), command.getPostalCode(), command.getRegion(), command.getLocality(), command.getStreetAddress());
        if (addressValidated.isValid()) {
            order.setDeliverAddress(addressValidated.value());
        } else {
            violations.addAll(addressValidated.errors());
        }

        Validated<DeliveryTime> deliveryTimeValidated = DeliveryTime.validator()
                .<DeliverOrderCommand>compose(c -> LocalDateTime.parse(c.getDeliveryTime(), DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")))
                .validate(command);

        if (deliveryTimeValidated.isValid()) {
            order.setDeliveryTime(deliveryTimeValidated.value());
        } else {
            violations.addAll(deliveryTimeValidated.errors());
        }

        if (violations.isEmpty()) {
            ConstraintViolations orderViolations = order.validateForDelivery();
            violations.addAll(orderViolations);
        }

        if (!violations.isEmpty()) {
            return Result.error(new OrderDeliveryException(violations));
        }

        // 注文を配送状態にし、永続化する。
        order.deliver();
        saveOrderPort.save(order);

        return Result.ok(new DeliveredOrderEvent(
                order.getOrderId(),
                order.getDeliverAddress(),
                order.getDeliveryTime()
        ));
    }
}
