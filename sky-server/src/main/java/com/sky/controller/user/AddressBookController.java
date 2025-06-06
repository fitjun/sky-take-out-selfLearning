package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "地址簿相关接口")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("/list")
    @ApiOperation("获取用户地址簿信息")
    public Result<List<AddressBook>> list() {
        List<AddressBook> BookList = addressBookService.list();
        return Result.success(BookList);
    }

    @PostMapping
    @ApiOperation("新增地址")
    public Result add(@RequestBody AddressBook addressBook) {
        addressBookService.add(addressBook);
        return Result.success();
    }

    @GetMapping("/default")
    @ApiOperation("查找默认地址")
    public Result<AddressBook> findDefault(){
        AddressBook addressBook = addressBookService.findDefault();
        return Result.success(addressBook);
    }
    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> findById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.findById(id);
        return Result.success(addressBook);
    }
}
