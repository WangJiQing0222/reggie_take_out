package com.takeOut.reggie.dto;

import com.takeOut.reggie.entity.OrderDetail;
import com.takeOut.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Orders {
    private List<OrderDetail> orderDetails;
}
