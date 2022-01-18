package org.practice.dao;

import org.practice.dif.annotation.Component;
import org.practice.pojo.Person;

@Component
public class UserDao {

    public Person getById(String id) {
        return new Person(id, "Amit");
    }
}
