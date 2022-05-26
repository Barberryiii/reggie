package com.xzkj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzkj.reggie.common.R;
import com.xzkj.reggie.dto.DishDto;
import com.xzkj.reggie.entity.Category;
import com.xzkj.reggie.entity.Dish;
import com.xzkj.reggie.service.CategoryService;
import com.xzkj.reggie.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        List<DishDto>list = dishDtoPage.getRecords();
        System.out.println("*****************");
        System.out.println(list.size());
        System.out.println("*****************");
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.isNotEmpty(name), Dish::getName, name)
          .orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo, qw);

        // 对象拷贝(忽略records)
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();



        // 拷贝records
        // low逼方法
/*        List<DishDto> dishRecords = new ArrayList<>();
        for (Dish dish : records) {
            DishDto dishDto = new DishDto();

            // 拷贝属性
            BeanUtils.copyProperties(dish, dishDto);

            // 根据菜品分类id查询菜品分类
            Long id = dish.getCategoryId();
            Category category = categoryService.getById(id);

            // 结果不为空则获取菜品分类名称并赋值
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            dishRecords.add(dishDto);
        }*/
        // 高级方法
        List<DishDto> dishRecords = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            // 拷贝属性
            BeanUtils.copyProperties(item, dishDto);

            // 根据菜品分类id查询菜品分类
            Long id = item.getCategoryId();
            Category category = categoryService.getById(id);

            // 结果不为空则获取菜品分类名称并赋值
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishRecords);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品信息成功");
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 根据ids批量修改状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable int status, Long[] ids){
        List<Dish> list = new ArrayList<>();
        for (Long id : ids) {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(status);
            list.add(dish);
        }
        dishService.updateBatchById(list);
        return R.success("");
    }

    /**
     * 根据ids批量逻辑删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] ids){
        dishService.removeWithFlavor(Arrays.asList(ids));

        return R.success("");
    }

    @GetMapping("/list")
    public R<List> list(Long categoryId){
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.eq(categoryId != null, Dish::getCategoryId, categoryId);
        qw.eq(Dish::getStatus, 1);
        qw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(qw);
        return R.success(list);
    }
}
