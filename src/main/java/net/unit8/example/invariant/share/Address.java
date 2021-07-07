package net.unit8.example.invariant.share;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.Arguments5Validator;
import am.ik.yavi.arguments.ArgumentsValidators;
import am.ik.yavi.arguments.StringValidator;
import am.ik.yavi.builder.StringValidatorBuilder;
import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.Validator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Optional;


@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Address implements IAddress {
    private static final StringValidator<String> countryValidator =  StringValidatorBuilder
            .of("country", c -> c.notBlank().lessThanOrEqual(2)).build();
    private static final StringValidator<String> postalCodeValidator = StringValidatorBuilder
            .of("postalCode", c -> c.notBlank().lessThanOrEqual(7)).build();
    private static final StringValidator<String> localityValidator = StringValidatorBuilder
            .of("locality", c -> c.notBlank().lessThanOrEqual(50)).build();
    private static final StringValidator<String> regionValidator = StringValidatorBuilder
            .of("region", c -> c.notBlank().lessThanOrEqual(50)).build();

    private static final Arguments1Validator<Optional<String>, Optional<String>> streetAddressValidator = ArgumentsValidators.liftOptional(StringValidatorBuilder
            .of("streetAddress", c -> c.lessThanOrEqual(50)).build());

    private final static Arguments5Validator<String, String, String, String, Optional<String>, Address> validator = ArgumentsValidators
            .split(countryValidator, postalCodeValidator, localityValidator, regionValidator, streetAddressValidator)
            .apply(((country, postalCode, locality, region, streetAddress) -> new Address(country, postalCode, locality, region, streetAddress.orElse(null))));

    private static final Validator<Address> addressValidator = ValidatorBuilder.of(Address.class)
            .constraintOnCondition((address, constraintGroup) -> address.getCountry().equals("JP"), b -> b
                    .constraint(Address::getPostalCode, "postalCode", c -> c.greaterThanOrEqual(7)))
            .build();

    String country;
    String postalCode;
    String region;
    String locality;
    String streetAddress;

    public static Arguments5Validator<String, String, String, String, Optional<String>, Address> validator() {
        return (a1, a2, a3, a4, a5, locale, constraintGroup) -> validator.validate(a1, a2, a3, a4, a5, locale, constraintGroup)
                .map(address -> addressValidator.applicative().validated(address));
    }

    public static Address of(String country, String postalCode, String region, String locality, Optional<String> streetAddress) {
        return validator.validated(country, postalCode, region, locality, streetAddress);
    }
}
