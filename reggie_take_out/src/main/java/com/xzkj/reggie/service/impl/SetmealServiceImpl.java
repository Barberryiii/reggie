package com.xzkj.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzkj.reggie.common.CustomException;
import com.xzkj.reggie.dto.SetmealDto;
import com.xzkj.reggie.entity.Setmeal;
import com.xzkj.reggie.entity.SetmealDish;
import com.xzkj.reggie.mapper.SetmealMapper;
import com.xzkj.reggie.service.SetmealDishService;
import com.xzkj.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐的基本信息， 操作setmeal，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        // 保存套餐和菜品的关联信息，操作setmeal_dish，执行insert操作
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态，确定是否可用删除
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.in(Setmeal::getId, ids);
        qw.eq(Setmeal::getStatus, 1);

        int count = this.count(qw);
        if(count > 0){
            // 如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        // 如果可以删除，先删除套餐表中的数据---setmeal
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.in(SetmealDish::getSetmealId, ids);
        // 删除关系表中的数据----setmeal_dish
        setmealDishService.remove(lqw);

    }

    /**
     * 修改套餐，同时需要修改套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);

        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();
        qw.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(qw);

        Long id = setmealDto.getId();

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(id);
        }

        setmealDishService.saveBatch(setmealDto.getSetmealDishes());
    }
}
