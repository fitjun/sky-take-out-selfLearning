package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    List<AddressBook> findAddressBook(AddressBook addressBook);

    void add(AddressBook addressBook);

    void update(AddressBook addressBook);

    @Delete("delete from address_book where id = #{id}")
    void delById(Long id);
}
