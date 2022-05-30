package com.xzkj.reggie.dto;

import com.xzkj.reggie.entity.OrderDetail;
import com.xzkj.reggie.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private List<OrderDetail> orderDetails;
	
}
