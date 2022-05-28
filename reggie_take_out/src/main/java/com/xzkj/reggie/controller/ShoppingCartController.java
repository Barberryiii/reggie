package com.xzkj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xzkj.reggie.common.BaseContext;
import com.xzkj.reggie.common.R;
import com.xzkj.reggie.entity.ShoppingCart;
import com.xzkj.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List> list(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, userId);
        qw.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(qw);
        return R.success(list);
    }

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        Long userId = BaseContext.getCurrentId();

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, userId);

        if(dishId != null){
            qw.eq(ShoppingCart::getDishId, dishId);
        }else{
            qw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }


        ShoppingCart one = shoppingCartService.getOne(qw);

        if(one !=null){
            one.setNumber(one.getNumber() + 1);
            shoppingCartService.updateById(one);
        }else{
            one = shoppingCart;

            one.setNumber(1);
            one.setUserId(userId);
            one.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(one);

        }

        return R.success(one);
    }

    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        Long userId = BaseContext.getCurrentId();

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, userId);

        if(dishId != null){
            qw.eq(ShoppingCart::getDishId, dishId);
        }else{
            qw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart one = shoppingCartService.getOne(qw);

        if(one != null){
            if(one.getNumber() > 1){
                one.setNumber(one.getNumber() - 1);
                shoppingCartService.updateById(one);
            }else{
                shoppingCartService.removeById(one.getId());
            }
        }else{
            return R.error("-1失败");
        }

        return R.success("-1成功");
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(qw);
        return R.success("清空购物车成功");
    }
}
