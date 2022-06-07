package com.xzkj.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzkj.reggie.entity.DishFlavor;
import com.xzkj.reggie.mapper.DishFlavorMapper;
import com.xzkj.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
