package com.takeOut.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeOut.reggie.common.CustomeException;
import com.takeOut.reggie.dto.OrderDto;
import com.takeOut.reggie.entity.*;
import com.takeOut.reggie.mapper.OrderMapper;
import com.takeOut.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author 小飞侠NO.1
 * @startTime 18:04:48
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     */
    @Transactional
    public void submit(Orders orders, HttpSession session) {
        //获得当前用户id
        Long userId = (Long) session.getAttribute("user");

        //查询当前用户购物车数据
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);

        if(shoppingCarts == null && shoppingCarts.size() == 0){
            throw new CustomeException("购物车为空，不能下单");
        }

        //查询用户数据
        User user = userService.getById(userId);

        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook == null){
            throw new CustomeException("用户地址信息有误，不能下单");
        }

        long orderId = IdWorker.getId();//订单号

        AtomicInteger amount = new AtomicInteger(0);//保证线程安全

        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        //向订单表中插入数据，一条数据
        this.save(orders);

        //向订单明细表中插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(wrapper);
    }

    /**
     * 订单展示
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public Page<OrderDto> page(int page, int pageSize, Long number, LocalDateTime beginTime, LocalDateTime endTime) {

        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrderDto> orderDtoPage = new Page<>();

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(number != null, Orders::getNumber, number);
        queryWrapper.ge(beginTime != null, Orders::getOrderTime, beginTime);
        queryWrapper.le(endTime != null, Orders::getCheckoutTime, endTime);
        queryWrapper.orderByAsc(endTime != null, Orders::getCheckoutTime);

        this.page(pageInfo, queryWrapper);//查询orders到pageInfo

        BeanUtils.copyProperties(pageInfo, orderDtoPage, "records");

        //设置records含有orderDetail 订单明细
        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> list = records.stream().map((item) -> {
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(item, orderDto);

            if (item != null) {
                LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(OrderDetail::getOrderId, item.getId());

                orderDto.setOrderDetails(orderDetailService.list(wrapper));//设置orderDetails
            }

            return orderDto;
        }).collect(Collectors.toList());

        orderDtoPage.setRecords(list);//设置records

        return orderDtoPage;
    }

    /**
     * 用户页面
     * @param page
     * @param pageSize
     * @param session
     * @return
     */
    @Override
    public Page<OrderDto> userPage(int page, int pageSize, HttpSession session) {

        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrderDto> orderDtoPage = new Page<>();

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, session.getAttribute("user"));
        this.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, orderDtoPage, "records");

        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> list = records.stream().map((item) -> {
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(item, orderDto);

            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getOrderId, item.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(wrapper);

            orderDto.setOrderDetails(orderDetails);

            return orderDto;
        }).collect(Collectors.toList());

        orderDtoPage.setRecords(list);

        return orderDtoPage;

    }

    /**
     * 再来一单
     * @param orders
     * @param session
     * @return
     */
    @Override
    public boolean again(Orders orders, HttpSession session) {
        //根据订单号 查询订单明细表
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(orders.getId() != null, OrderDetail::getOrderId, orders.getId());
        List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper);

        List<ShoppingCart> shoppingCartList = orderDetails.stream().map((item) -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            //将订单明细表的数据添加到购物车里
            shoppingCart.setUserId((Long) session.getAttribute("user"));
            shoppingCart.setName(item.getName());
            shoppingCart.setImage(item.getImage());
            shoppingCart.setDishId(item.getDishId());
            shoppingCart.setSetmealId(item.getSetmealId());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());

            return shoppingCart;
        }).collect(Collectors.toList());

        boolean flag = shoppingCartService.saveBatch(shoppingCartList);

        this.removeById(orders.getId());
        orderDetailService.remove(queryWrapper);


        return flag;
    }
}
