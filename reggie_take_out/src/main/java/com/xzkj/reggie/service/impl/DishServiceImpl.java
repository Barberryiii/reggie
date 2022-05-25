package com.xzkj.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzkj.reggie.dto.DishDto;
import com.xzkj.reggie.entity.Dish;
import com.xzkj.reggie.entity.DishFlavor;
import com.xzkj.reggie.mapper.DishMapper;
import com.xzkj.reggie.service.DishFlavorService;
import com.xzkj.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);

        Long id = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();

        for (DishFlavor flavor : flavors) {
            flavor.setDishId(id);
        }

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品和对应的口味信息信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        // 查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper();
        qw.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavor = dishFlavorService.list(qw);

        dishDto.setFlavors(flavor);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新dish表基本信息
        this.updateById(dishDto);

        // 清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(qw);

        // 添加当前提交过来的口味数据---dish_flavor表的insert操作
        Long id = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();

        for (DishFlavor flavor : flavors) {
            flavor.setDishId(id);
        }

        dishFlavorService.saveBatch(flavors);
    }
}
