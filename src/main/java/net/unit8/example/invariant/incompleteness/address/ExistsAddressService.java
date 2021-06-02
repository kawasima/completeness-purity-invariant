package net.unit8.example.invariant.incompleteness.address;

import net.unit8.example.invariant.incompleteness.ExistsAddressPort;
import net.unit8.example.invariant.share.Address;

public class ExistsAddressService implements ExistsAddressPort {
    @Override
    public ExistingAddress exists(Address address) {
        // 実際には外部サービスを呼び出す。
        return new ExistingAddress(address);
    }
}
