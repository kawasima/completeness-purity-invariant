package net.unit8.example.invariant.typepower;

import am.ik.yavi.core.Validated;
import net.unit8.example.invariant.share.Address;
import net.unit8.example.invariant.share.DeliveryTime;

public class InProgressOrder extends AbstractOrder {
    /**
     * 住所と配送日を受け取り配送処理を行う。
     *
     * @param address 配送先住所
     * @param deliveryTime 配達日時
     * @return 注文のValidated型
     */
    public Validated<DeliveringOrder> deliver(Address address, DeliveryTime deliveryTime) {
        return DeliveringOrder.of(this, address, deliveryTime);
    }
}
