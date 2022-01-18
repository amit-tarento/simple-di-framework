package org.practice.service;

import org.practice.dao.UserAddressDao;
import org.practice.dao.UserDao;
import org.practice.dif.annotation.Component;
import org.practice.dif.annotation.Inject;
import org.practice.pojo.Address;
import org.practice.pojo.Person;

@Component
public class UserService {

    @Inject
    private UserDao userDao;

    @Inject
    private UserAddressDao userAddressDao;

    public Person getPerson(String id) {
        Person person = userDao.getById(id);
        Address address = userAddressDao.getById(id);
        person.setAddress(address);
        return person;
    }
}
