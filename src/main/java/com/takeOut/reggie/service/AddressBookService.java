package com.takeOut.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeOut.reggie.entity.AddressBook;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author 小飞侠NO.1
 * @startTime 14:44:08
 */
public interface AddressBookService extends IService<AddressBook> {
    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    AddressBook setDefault(AddressBook addressBook, HttpSession session);

    /**
     * 查询默认地址
     */
    AddressBook getDefault(HttpSession session);

    /**
     * 查询指定用户的全部地址
     */
    List<AddressBook> list(AddressBook addressBook, HttpSession session);
}
