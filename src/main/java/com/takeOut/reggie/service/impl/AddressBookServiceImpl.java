package com.takeOut.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeOut.reggie.entity.AddressBook;
import com.takeOut.reggie.mapper.AddressBookMapper;
import com.takeOut.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author 小飞侠NO.1
 * @startTime 14:45:53
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
