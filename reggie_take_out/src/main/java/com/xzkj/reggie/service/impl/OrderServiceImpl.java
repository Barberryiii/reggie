package com.xzkj.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzkj.reggie.entity.Orders;
import com.xzkj.reggie.mapper.OrderMapper;
import com.xzkj.reggie.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
}
