package net.unit8.example.invariant.incompleteness.address;

import net.unit8.example.invariant.share.Address;
import net.unit8.example.invariant.share.IAddress;

public class ExistingAddress implements IAddress {
    private final Address address;
    ExistingAddress(Address address) {
        this.address = address;
    }

    @Override
    public String getCountry() {
        return address.getCountry();
    }

    @Override
    public String getPostalCode() {
        return address.getPostalCode();
    }

    @Override
    public String getRegion() {
        return address.getRegion();
    }

    @Override
    public String getLocality() {
        return address.getLocality();
    }

    @Override
    public String getStreetAddress() {
        return address.getStreetAddress();
    }
}
