package net.unit8.example.invariant.share;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.Validated;
import am.ik.yavi.core.Validator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;


@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Address {
    private final static Validator<Address> validator = ValidatorBuilder.<Address>of()
            .constraint(Address::getCountry, "country", c -> c.notBlank()
                    .lessThanOrEqual(2))
            .constraint(Address::getCountry, "postalCode", c -> c.notBlank()
                    .lessThanOrEqual(7))
            .constraint(Address::getCountry, "locality", c -> c.notBlank()
                    .lessThanOrEqual(50))
            .constraint(Address::getCountry, "region", c -> c.notBlank()
                    .lessThanOrEqual(50))
            .constraint(Address::getCountry, "streetAddress", c -> c.lessThanOrEqual(50))
            .build();

    String country;
    String postalCode;
    String region;
    String locality;
    String streetAddress;

    public static Validated<Address> of(String country, String postalCode, String region, String locality, String streetAddress) {
        return validator.applicative().validate(new Address(country, postalCode, region, locality, streetAddress));
    }
}
