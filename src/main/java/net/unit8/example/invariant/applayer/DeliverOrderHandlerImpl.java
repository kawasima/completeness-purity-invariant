package net.unit8.example.invariant.applayer;

import am.ik.yavi.core.ConstraintViolations;
import am.ik.yavi.core.Validated;
import am.ik.yavi.fn.Validations;
import am.ik.yavi.message.SimpleMessageFormatter;
import io.fries.result.Result;
import lombok.Value;
import net.unit8.example.invariant.share.*;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

import static net.unit8.example.invariant.share.OrderConstraint.DELIVERY_WEEKDAY;
import static net.unit8.example.invariant.share.OrderConstraint.SHIPPING_JAPAN_ONLY;

/**
 * ドメインが不変条件を満たさない瞬間を作らないために、Application Layerでチェックしてから、配送処理を行うパターン。
 *
 * ビジネスルールチェックがドメイン層から離れるので、ドメインの完全性が失われる。
 * さらには、Application Layerの実装者がチェックを忘れてしまうと、結局、Invalidな状態のOrderが出来ることには変わりがない。
 */
@Component
public class DeliverOrderHandlerImpl implements DeliverOrderHandler {
    /**
     * 配送に関する情報を保持しビジネスルールのチェックを行うためのクラス。
     */
    @Value
    private static class DeliveryInformation {
        Address address;
        DeliveryTime deliveryTime;

        boolean modelValidity = false;

        private DeliveryInformation(Address address, DeliveryTime deliveryTime) {
            this.address = address;
            this.deliveryTime = deliveryTime;
        }

        ConstraintViolations validate(EnumSet<OrderConstraint> constraints) {
            SimpleMessageFormatter messageFormatter = new SimpleMessageFormatter();
            ConstraintViolations violations = new ConstraintViolations();
            if (constraints.contains(SHIPPING_JAPAN_ONLY) && !address.getCountry().equals("JP")) {
                violations.add(SHIPPING_JAPAN_ONLY.getConstraintViolation());
            }
            // 平日のみ配送の注文は、配送日が平日でなくてはならない
            if (constraints.contains(DELIVERY_WEEKDAY) && deliveryTime.isHoliday()) {
                violations.add(DELIVERY_WEEKDAY.getConstraintViolation());
            }
            return violations;        }
    }

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
        Validated<DeliveryInformation> deliveryValidated = Validations.combine(addressValidated, deliveryTimeValidated)
                .apply(DeliveryInformation::new);

        // Address, DeliveryTimeが不変条件を満たさないならば、エラーを返す
        if (!deliveryValidated.isValid()) {
            return Result.error(new OrderDeliveryException(deliveryValidated.errors()));
        }

        DeliveryInformation deliveryInformation = deliveryValidated.value();
        // AddressとDeliveryTimeがOrderの制約を満たすかをチェックする。満たさなければエラーを返す。
        ConstraintViolations violations = deliveryInformation.validate(order.getConstraints());
        if (!violations.isEmpty()) {
            return Result.error(new OrderDeliveryException(violations));
        }

        // 注文を配送状態にし、永続化する。
        order.deliver(deliveryInformation.getAddress(), deliveryInformation.getDeliveryTime());
        saveOrderPort.save(order);

        return Result.ok(new DeliveredOrderEvent(
                order.getOrderId(),
                order.getDeliverAddress(),
                order.getDeliveryTime()
        ));
    }
}
