package net.unit8.example.invariant.share;

import lombok.Value;

import java.io.Serializable;
import java.util.Optional;

@Value
public class DeliverOrderCommand implements Serializable {
    String orderId;
    String country;
    String postalCode;
    String region;
    String locality;
    String streetAddress;
    String deliveryTime;

    public Optional<String> getStreetAddress() {
        return Optional.ofNullable(streetAddress);
    }
}
