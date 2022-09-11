package com.takeOut.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeOut.reggie.common.CustomeException;
import com.takeOut.reggie.entity.Category;
import com.takeOut.reggie.entity.Dish;
import com.takeOut.reggie.entity.Setmeal;
import com.takeOut.reggie.mapper.CategoryMapper;
import com.takeOut.reggie.service.CategoryService;
import com.takeOut.reggie.service.DishService;
import com.takeOut.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 小飞侠NO.1
 * @startTime 17:38:04
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否已经关联了一个菜品，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        if(count1 > 0){
            //已经关联菜品，抛出一个业务异常
            throw new CustomeException("当前分类下关联了菜品，不能删除");
        }


        //查询当前分类是否已经关联了一个套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);

        if(count2 > 0){
            //已经关联套餐，抛出一个业务异常
            throw new CustomeException("当前分类下关联了套餐，不能删除");
        }


        //正常删除分类
        super.removeById(id);
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page<Category> page(int page, int pageSize) {
        //分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);

        //进行分页查询
        this.page(pageInfo, queryWrapper);

        return pageInfo;
    }

    /**
     * 根据条件查询分类数据   菜品分类
     * @param category
     * @return
     */
    @Override
    public List<Category> list(Category category) {
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = this.list(queryWrapper);

        return list;
    }
}
