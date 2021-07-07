package net.unit8.example.invariant.incompleteness;

import am.ik.yavi.arguments.Arguments;
import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.ArgumentsValidators;
import am.ik.yavi.builder.ArgumentsValidatorBuilder;
import am.ik.yavi.core.ConstraintViolation;
import am.ik.yavi.core.Validated;
import am.ik.yavi.fn.Validations;
import io.fries.result.Result;
import net.unit8.example.invariant.incompleteness.address.ExistingAddress;
import net.unit8.example.invariant.share.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class DeliverOrderHandlerImpl implements DeliverOrderHandler {
    private final LoadOrderPort loadOrderPort;
    private final ExistsAddressPort existsAddressPort;
    private final SaveOrderPort saveOrderPort;

    public DeliverOrderHandlerImpl(LoadOrderPort loadOrderPort, ExistsAddressPort existsAddressPort, SaveOrderPort saveOrderPort) {
        this.loadOrderPort = loadOrderPort;
        this.existsAddressPort = existsAddressPort;
        this.saveOrderPort = saveOrderPort;
    }

    @Override
    public Result<DeliveredOrderEvent> handle(DeliverOrderCommand command) {
        OrderId orderId = new OrderId(command.getOrderId());
        final Order order = loadOrderPort.load(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        Validated<Address> addressValidated = Address.validator()
                .<DeliverOrderCommand>compose(c -> Arguments.of(c.getCountry(), c.getPostalCode(), c.getRegion(), c.getLocality(), c.getStreetAddress()))
                .validate(command);

        Validated<DeliveryTime> deliveryTimeValidated = DeliveryTime.validator()
                .<DeliverOrderCommand>compose(c -> LocalDateTime.parse(c.getDeliveryTime(), DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")))
                .validate(command);


        // 注文を配送状態にし、永続化する。
        // deliverの中で、ビジネスルールがチェックされる。
        Optional<List<ConstraintViolation>> violations = Validations.combine(addressValidated, deliveryTimeValidated).apply((address, deliveryTime) -> {
            // 送付先住所の存在チェック
            // ビジネスルールがドメイン層の外でチェックされるので完全性が失われる。
            // 完全性が失われるデメリットの1つである、このチェックを呼び忘れてOrder#deliverが呼べてしまう点に
            // ついては、存在する住所の型(ここではExistingAddress)を別に作ることで防げる。
            ExistingAddress existingAddress = existsAddressPort.exists(address);
            // 配送処理
            return order.deliver(existingAddress, deliveryTime);
        }).fold(Optional::of,
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
