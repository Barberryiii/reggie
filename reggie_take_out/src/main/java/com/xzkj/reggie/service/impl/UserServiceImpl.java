package com.xzkj.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzkj.reggie.entity.User;
import com.xzkj.reggie.mapper.UserMapper;
import com.xzkj.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
