package com.xzkj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzkj.reggie.common.BaseContext;
import com.xzkj.reggie.common.R;
import com.xzkj.reggie.dto.OrdersDto;
import com.xzkj.reggie.entity.OrderDetail;
import com.xzkj.reggie.entity.Orders;
import com.xzkj.reggie.service.OrderDetailService;
import com.xzkj.reggie.service.OrderService;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 查询用户订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize){
        Page<Orders> ordersPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> ordersQw = new LambdaQueryWrapper<>();
        ordersQw.orderByDesc(Orders::getOrderTime);

        orderService.page(ordersPage, ordersQw);

        Page<OrdersDto> ordersDtoPage = new Page<>();

        BeanUtils.copyProperties(ordersPage, ordersDtoPage, "records");

        List<Orders> OrdersRecords = ordersPage.getRecords();

        List<OrdersDto> ordersDtoList = OrdersRecords.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();

            BeanUtils.copyProperties(item, ordersDto);

            LambdaQueryWrapper<OrderDetail> orderDetailQw = new LambdaQueryWrapper<>();
            orderDetailQw.eq(OrderDetail::getOrderId, item.getId());

            ordersDto.setOrderDetails(orderDetailService.list(orderDetailQw));

            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(ordersDtoList);

        return R.success(ordersDtoPage);
    }

    @GetMapping("/page")
    public R<Page> page(@RequestParam Map map){
        int page = map.containsKey("page") ? Integer.parseInt(map.get("page").toString()) : null;
        int pageSize = map.containsKey("pageSize") ? Integer.parseInt(map.get("pageSize").toString()) : null;
        Long orderId = map.containsKey("number") ? new Long(map.get("number").toString()) : null;
        String beginTime = map.containsKey("beginTime") ? map.get("beginTime").toString() : null;
        String endTime = map.containsKey("endTime") ? map.get("endTime").toString() : null;

        Page<Orders> ordersPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> ordersQw = new LambdaQueryWrapper<>();
        ordersQw.eq(orderId != null , Orders::getId, orderId);
        ordersQw.between(beginTime != null && endTime != null, Orders::getOrderTime, beginTime, endTime);

        orderService.page(ordersPage, ordersQw);

        return R.success(ordersPage);
    }
}
