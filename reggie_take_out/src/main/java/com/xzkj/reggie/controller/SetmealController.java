package com.xzkj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzkj.reggie.common.R;
import com.xzkj.reggie.dto.SetmealDto;
import com.xzkj.reggie.entity.Category;
import com.xzkj.reggie.entity.Dish;
import com.xzkj.reggie.entity.Setmeal;
import com.xzkj.reggie.entity.SetmealDish;
import com.xzkj.reggie.service.CategoryService;
import com.xzkj.reggie.service.SetmealDishService;
import com.xzkj.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Autowired
    private CacheManager cacheManager;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
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

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> delete(Long[] ids){
        setmealService.removeWithDish(Arrays.asList(ids));

        return R.success("套餐数据删除成功");
    }

    /**
     * 根据id查询套餐数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        Setmeal setmeal = setmealService.getById(id);

        SetmealDto setmealDto = new SetmealDto();

        BeanUtils.copyProperties(setmeal, setmealDto);

        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();
        qw.eq(SetmealDish::getSetmealId, setmeal.getId());

        setmealDto.setSetmealDishes(setmealDishService.list(qw));

        return R.success(setmealDto);
    }

    /**
     * 批量启售/停售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> changeStatus(@PathVariable int status, Long[] ids){
        List<Setmeal> list = new ArrayList<>();
        for (Long id : ids) {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(status);
            list.add(setmeal);
        }
        setmealService.updateBatchById(list);
        return R.success("");
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);

        return R.success("修改套餐成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.getCategoryId() + '_' + #setmeal.getStatus()")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }
}
