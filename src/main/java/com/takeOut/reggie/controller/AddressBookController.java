package com.takeOut.reggie.controller;

import com.takeOut.reggie.common.R;
import com.takeOut.reggie.entity.AddressBook;
import com.takeOut.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 地址簿管理
 * @author 小飞侠NO.1
 * @startTime 14:46:36
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook, HttpSession session) {
        log.info("addressBook:{}", addressBook);

        addressBook.setUserId((Long) session.getAttribute("user"));
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook, HttpSession session) {
        log.info("addressBook:{}", addressBook);

        addressBook = addressBookService.setDefault(addressBook, session);

        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public R<AddressBook> getDefault(HttpSession session) {
        log.info("查询默认地址");
        AddressBook addressBook =  addressBookService.getDefault(session);


        return addressBook == null ? R.error("没有找到该对象") : R.success(addressBook);
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook, HttpSession session) {
        log.info("addressBook:{}", addressBook);

        List<AddressBook> list = addressBookService.list(addressBook, session);

        return R.success(list);
    }
}
