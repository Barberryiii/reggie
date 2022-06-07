package com.xzkj.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzkj.reggie.entity.AddressBook;
import com.xzkj.reggie.mapper.AddressBookMapper;
import com.xzkj.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
