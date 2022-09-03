package com.takeOut.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeOut.reggie.dto.SetmealDto;
import com.takeOut.reggie.entity.Setmeal;

import java.util.List;

/**
 * @author 小飞侠NO.1
 * @startTime 19:16:11
 */
public interface SetmealService extends IService<Setmeal> {

    //新增套餐，同时需要保存套餐和菜品的关联关系
    public void saveWithDish(SetmealDto setmealDto);

    //删除套餐，同时需要删除套餐和菜品的关联数据
    public void removeWithDish(List<Long> ids);
}
