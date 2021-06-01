package net.unit8.example.invariant.tryexecute;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.Validated;
import am.ik.yavi.core.Validator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.unit8.example.invariant.share.*;

import java.util.EnumSet;

import static net.unit8.example.invariant.share.OrderConstraint.DELIVERY_WEEKDAY;
import static net.unit8.example.invariant.share.OrderConstraint.SHIPPING_JAPAN_ONLY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Order {
    private static final Validator<Order> validator = ValidatorBuilder.<Order>of()
            .constraintOnCondition((order, constraintGroup) -> order.getConstraints().contains(SHIPPING_JAPAN_ONLY),
                    c -> c.constraintOnTarget(o -> o.getDeliverAddress().getCountry().equals("JP"), SHIPPING_JAPAN_ONLY.getConstraintViolation().name(),
                            SHIPPING_JAPAN_ONLY.getViolationMessage()))
            .constraintOnCondition((order, constraintGroup) -> order.getConstraints().contains(DELIVERY_WEEKDAY),
                    c -> c.constraintOnTarget(o -> !o.getDeliveryTime().isHoliday(), DELIVERY_WEEKDAY.getConstraintViolation().name(),
                            DELIVERY_WEEKDAY.getViolationMessage()))
            .build();

    public static Order ofInProgress(OrderId orderId, EnumSet<OrderConstraint> constraint) {
        Order order = new Order();
        order.orderId = orderId;
        order.status = OrderStatus.IN_PROGRESS;
        order.constraints = constraint;
        return order;
    }
    @Getter
    OrderId orderId;

    @Getter
    OrderStatus status = OrderStatus.IN_PROGRESS;

    @Getter
    EnumSet<OrderConstraint> constraints;

    @Getter
    Address deliverAddress;

    @Getter
    DeliveryTime deliveryTime;



    protected OrderMemento saveState() {
        return new OrderMemento(status, deliverAddress, deliveryTime);
    }

    protected void restoreState(OrderMemento orderMemento) {
        status = orderMemento.getOrderStatus();
        deliverAddress = orderMemento.getAddress();
        deliveryTime = orderMemento.getDeliveryTime();
    }

    /**
     * 住所と配送日を受け取り配送処理を行う。
     *
     * @param address 配送先住所
     * @param deliveryTime 配達日時
     * @return 注文のValidated型
     */
    public Validated<Order> deliver(Address address, DeliveryTime deliveryTime) {
        OrderMemento previousState = saveState();
        this.deliverAddress = address;
        this.deliveryTime = deliveryTime;
        status = OrderStatus.DELIVERING;

        Validated<Order> validatedOrder = validator.applicative().validate(this);
        if (!validatedOrder.isValid()) {
            restoreState(previousState);
        }

        return validatedOrder;
    }
}
