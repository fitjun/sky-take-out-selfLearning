package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    List<AddressBook> list();

    void add(AddressBook addressBook);

    AddressBook findDefault();

    AddressBook findById(Long id);

    void update(AddressBook addressBook);

    void changeDefault(AddressBook addressBook);
}
