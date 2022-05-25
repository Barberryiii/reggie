package com.xzkj.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xzkj.reggie.dto.DishDto;
import com.xzkj.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);
}
