package net.unit8.example.invariant.isvalid;

import io.fries.result.Result;
import net.unit8.example.invariant.share.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Optional;

import static net.unit8.example.invariant.share.OrderConstraint.DELIVERY_WEEKDAY;
import static net.unit8.example.invariant.share.OrderConstraint.SHIPPING_JAPAN_ONLY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DeliverOrderHandlerImplTest {
    DeliverOrderHandler sut;

    @BeforeEach
    void setup() {
        LoadOrderPort loadOrderPort = mock(LoadOrderPort.class);
        when(loadOrderPort.load(any())).thenReturn(Optional.of(Order.ofInProgress(
                new OrderId("order001"),
                EnumSet.of(SHIPPING_JAPAN_ONLY, DELIVERY_WEEKDAY)
        )));
        SaveOrderPort saveOrderPort = mock(SaveOrderPort.class);
        sut = new DeliverOrderHandlerImpl(loadOrderPort, saveOrderPort);
    }

    @Test
    void violateForShippingJapanOnly() {
        Result<DeliveredOrderEvent> event = sut.handle(new DeliverOrderCommand(
                "order001",
                "US",
                "0010001",
                "NewYork",
                "Wall street",
                null,
                "2021/06/04 20:00"
        ));
        assertThat(event.isError()).isTrue();
        assertThat(event.getError()).isInstanceOf(OrderDeliveryException.class);
        assertThat(event.getError().getMessage()).contains("only for Japan");
    }

    @Test
    void violateWeekday() {
        Result<DeliveredOrderEvent> event = sut.handle(new DeliverOrderCommand(
                "order001",
                "JP",
                "0010001",
                "Tokyo",
                "Suginami-ku",
                null,
                "2021/06/05 20:00"
        ));
        assertThat(event.isError()).isTrue();
        assertThat(event.getError()).isInstanceOf(OrderDeliveryException.class);
        assertThat(event.getError().getMessage()).contains("weekday");
    }
    @Test
    void successful() {
        Result<DeliveredOrderEvent> event = sut.handle(new DeliverOrderCommand(
                "order001",
                "JP",
                "0010001",
                "Tokyo",
                "Suginami-ku",
                null,
                "2021/06/04 20:00"
        ));
        assertThat(event.isOk()).isTrue();
        assertThat(event.get()).hasFieldOrProperty("address").matches(e->e.getAddress().getRegion().equals("Tokyo"));
    }
}