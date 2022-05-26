package com.xzkj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzkj.reggie.common.R;
import com.xzkj.reggie.dto.SetmealDto;
import com.xzkj.reggie.entity.Category;
import com.xzkj.reggie.entity.Setmeal;
import com.xzkj.reggie.service.CategoryService;
import com.xzkj.reggie.service.SetmealDishService;
import com.xzkj.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        // 分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        // 条件查询
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.like(name != null, Setmeal::getName, name);
        qw.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, qw);

        // 对象拷贝(忽略records)
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");

        List<Setmeal> records = pageInfo.getRecords();

        // 拷贝records
        List<SetmealDto> dtoRecords = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();

            // 拷贝属性
            BeanUtils.copyProperties(item, setmealDto);

            // 根据菜品分类id查询菜品分类
            Category category = categoryService.getById(item.getCategoryId());

            // 结果不为空则获取菜品分类名称并赋值
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }

            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dtoRecords);

        return R.success(dtoPage);
    }
}
