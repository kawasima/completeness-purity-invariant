package net.unit8.example.invariant.typepower;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.Validated;
import am.ik.yavi.core.Validator;
import lombok.Getter;
import net.unit8.example.invariant.share.Address;
import net.unit8.example.invariant.share.DeliveryTime;

import static net.unit8.example.invariant.share.OrderConstraint.DELIVERY_WEEKDAY;
import static net.unit8.example.invariant.share.OrderConstraint.SHIPPING_JAPAN_ONLY;

public class DeliveringOrder extends AbstractOrder {
    private static final Validator<DeliveringOrder> validator = ValidatorBuilder.<DeliveringOrder>of()
            .constraintOnCondition((order, constraintGroup) -> order.getConstraints().contains(SHIPPING_JAPAN_ONLY),
                    c -> c.constraintOnTarget(o -> o.getDeliverAddress().getCountry().equals("JP"), SHIPPING_JAPAN_ONLY.getConstraintViolation().name(),
                            SHIPPING_JAPAN_ONLY.getViolationMessage()))
            .constraintOnCondition((order, constraintGroup) -> order.getConstraints().contains(DELIVERY_WEEKDAY),
                    c -> c.constraintOnTarget(o -> !o.getDeliveryTime().isHoliday(), DELIVERY_WEEKDAY.getConstraintViolation().name(),
                            DELIVERY_WEEKDAY.getViolationMessage()))
            .build();

    private DeliveringOrder(InProgressOrder inProgressOrder, Address deliverAddress, DeliveryTime deliveryTime) {
        this.orderId = inProgressOrder.getOrderId();
        this.constraints = inProgressOrder.getConstraints();
        this.deliverAddress = deliverAddress;
        this.deliveryTime = deliveryTime;
    }

    static Validated<DeliveringOrder> of(InProgressOrder inProgressOrder, Address deliverAddress, DeliveryTime deliveryTime) {
        DeliveringOrder deliveringOrder = new DeliveringOrder(inProgressOrder, deliverAddress, deliveryTime);
        return validator.applicative().validate(deliveringOrder);
    }

    @Getter
    Address deliverAddress;

    @Getter
    DeliveryTime deliveryTime;

}
