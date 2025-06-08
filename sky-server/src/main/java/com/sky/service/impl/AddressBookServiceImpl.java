package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.exception.AddressBookBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Override
    public List<AddressBook> list() {
        AddressBook addressBook = new AddressBook();
        List<AddressBook> bookList = addressBookMapper.findAddressBook(addressBook);
        return bookList;
    }

    @Override
    public void add(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        //新加入的都设置为默认地址
        addressBook.setIsDefault(1);
        addressBookMapper.add(addressBook);
    }

    @Override
    public AddressBook findDefault() {
        AddressBook addressBook = new AddressBook();
        addressBook.setIsDefault(1);
        List<AddressBook> addressBook1 = addressBookMapper.findAddressBook(addressBook);
        if(addressBook1.size()>0 &&addressBook1!=null){
            return addressBook1.get(0);
        }
        return null;
    }

    @Override
    public AddressBook findById(Long id) {
        AddressBook addressBook = new AddressBook();
        addressBook.setId(id);
        List<AddressBook> addressBook1 = addressBookMapper.findAddressBook(addressBook);
        return addressBook1.get(0);
    }

    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeDefault(AddressBook addressBook) {
        AddressBook addressBook1 = new AddressBook();
        addressBook1.setIsDefault(1);
        List<AddressBook> addressBook2 = addressBookMapper.findAddressBook(addressBook1);
        if (!addressBook2.isEmpty()){
            AddressBook def = addressBook2.get(0);
            def.setIsDefault(0);
            addressBookMapper.update(def);
        }
        //没有默认值
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);
    }

    @Override
    public void delById(Long id) {
        log.info("删除地址" + id);
        addressBookMapper.delById(id);
    }
}
