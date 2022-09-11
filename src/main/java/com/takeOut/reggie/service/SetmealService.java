package com.takeOut.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    /**
     * 菜品停售或者起售
     * @param status
     * @param ids
     * @return
     */
    public boolean changeStatus(Integer status, List<Long> ids);

    /**
     * 根据setmealId查询
     * @param id
     * @return
     */
    public SetmealDto getByIdWithDish(Long id);

    //修改套餐 套餐菜品表
    public void updateWithDish(SetmealDto setmealDto);

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    Page<SetmealDto> page(int page, int pageSize, String name);

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

}
