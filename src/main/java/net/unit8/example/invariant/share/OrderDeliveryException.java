package net.unit8.example.invariant.share;

import am.ik.yavi.core.ConstraintViolation;

import java.util.List;
import java.util.stream.Collectors;

public class OrderDeliveryException extends RuntimeException{
    private final List<ConstraintViolation> violations;

    public OrderDeliveryException(List<ConstraintViolation> violations) {
        super(violations.stream().map(ConstraintViolation::message).collect(Collectors.joining(",")));
        this.violations = violations;
    }

    public List<ConstraintViolation> reasons() {
        return violations;
    }
}
