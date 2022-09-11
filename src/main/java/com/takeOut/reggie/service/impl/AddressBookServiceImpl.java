package com.takeOut.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeOut.reggie.entity.AddressBook;
import com.takeOut.reggie.mapper.AddressBookMapper;
import com.takeOut.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author 小飞侠NO.1
 * @startTime 14:45:53
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    public AddressBook setDefault(AddressBook addressBook, HttpSession session) {

        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, session.getAttribute("user"));
        wrapper.set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        this.update(wrapper);

        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        this.updateById(addressBook);

        return null;
    }

    /**
     * 查询默认地址
     */
    public AddressBook getDefault(HttpSession session) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, session.getAttribute("user"));
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = this.getOne(queryWrapper);

        return addressBook;
    }

    /**
     * 查询指定用户的全部地址
     */
    public List<AddressBook> list(AddressBook addressBook, HttpSession session) {
        addressBook.setUserId((Long) session.getAttribute("user"));

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        List<AddressBook> list = this.list(queryWrapper);

        return list;
    }


}
