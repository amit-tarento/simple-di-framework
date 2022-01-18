package org.practice.dao;

import org.practice.dif.annotation.Component;
import org.practice.pojo.Address;

@Component
public class UserAddressDao {

    public Address getById(String userId) {
        return new Address("addrLine1", "addrLine2", "Bangalore", "560035");
    }
}
