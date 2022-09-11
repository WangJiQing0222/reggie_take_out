package com.takeOut.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeOut.reggie.dto.OrderDto;
import com.takeOut.reggie.entity.Orders;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * @author 小飞侠NO.1
 * @startTime 18:03:20
 */
public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders, HttpSession session);

    /**
     * 订单展示
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    Page<OrderDto> page(int page, int pageSize, Long number, LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 用户页面
     * @param page
     * @param pageSize
     * @param session
     * @return
     */
    Page<OrderDto> userPage(int page, int pageSize, HttpSession session);

    /**
     * 再来一单
     * @param orders
     * @param session
     * @return
     */
    boolean again(Orders orders, HttpSession session);
}
