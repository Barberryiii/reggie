package com.xzkj.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzkj.reggie.entity.OrderDetail;
import com.xzkj.reggie.mapper.OrderDetailMapper;
import com.xzkj.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
