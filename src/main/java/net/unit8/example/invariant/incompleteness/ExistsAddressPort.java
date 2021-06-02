package net.unit8.example.invariant.incompleteness;

import net.unit8.example.invariant.incompleteness.address.ExistingAddress;
import net.unit8.example.invariant.share.Address;

public interface ExistsAddressPort {
    ExistingAddress exists(Address address);
}
