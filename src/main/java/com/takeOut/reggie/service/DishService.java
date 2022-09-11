package com.takeOut.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeOut.reggie.dto.DishDto;
import com.takeOut.reggie.entity.Dish;

import java.util.List;

/**
 * @author 小飞侠NO.1
 * @startTime 19:15:13
 */
public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表 dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息，同时更新口味信息
    public void updateWithFlavor(DishDto dishDto);

    //删除ids中对应的菜品(停售) 和对应的口味
    public boolean remove(List<Long> ids);

    //改变状态
    public boolean changeStatus(Integer status, List<Long> ids);

    /**
     * 菜品信息分页查询
     * Dish里面没有categoryName
     * DishDto里面有categoryName, 需要单独对records进行操作，
     * 通过DishDto继承的Dish的categoryId，查到category数据，获得categoryName
     *
     * 设置categoryName，使得records里面有categoryName
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    Page<DishDto> page(int page, int pageSize, String name);

    /**
     * 根据条件查询对应的菜品数据    DishDto在Dish原来的基础上添加了flavors和categoryName
     * @param dish
     * @return
     */
    List<DishDto> list(Dish dish);
}
