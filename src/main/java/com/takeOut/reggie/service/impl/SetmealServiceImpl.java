package com.takeOut.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeOut.reggie.common.CustomeException;
import com.takeOut.reggie.dto.SetmealDto;
import com.takeOut.reggie.entity.Category;
import com.takeOut.reggie.entity.Setmeal;
import com.takeOut.reggie.entity.SetmealDish;
import com.takeOut.reggie.mapper.SetmealMapper;
import com.takeOut.reggie.service.CategoryService;
import com.takeOut.reggie.service.SetmealDishService;
import com.takeOut.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 小飞侠NO.1
 * @startTime 19:18:38
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的基本信息，操作setmeal_dish，执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }


    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * 注意：删除套餐不一定要删除对应的菜品
     * @param ids
     */
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);
        if (count > 0) {
            //如果不能删除，抛出业务异常
            throw new CustomeException("套餐正在售卖中，不能删除");
        }


        //如果可以删除，先删除套餐表中的数据--setmeal
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId, ids);
        //删除关系表中的数据--setmeal_dish
        setmealDishService.remove(queryWrapper1);
    }

    /**
     * 菜品停售或者起售
     * @param status
     * @param ids
     * @return
     */
    @Override
    public boolean changeStatus(Integer status, List<Long> ids) {
        //停售
        if(status == 0) {
            List<Setmeal> setmeals = super.listByIds(ids);
            if(setmeals != null){
                for(Setmeal setmeal : setmeals)
                    setmeal.setStatus(0);

                super.updateBatchById(setmeals);//批量更改为停售
                return true;
            }

        }else{      //启售
            List<Setmeal> setmeals = super.listByIds(ids);
            if(setmeals != null){
                for(Setmeal setmeal : setmeals)
                    setmeal.setStatus(1);

                super.updateBatchById(setmeals);//批量更改为起售
                return true;
            }
        }

        return false;
    }

    /**
     * 根据setmealId查询
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = this.getById(id);//根据setmealId查询setmeal
        SetmealDto setmealDto = new SetmealDto();//new一个 dto

        BeanUtils.copyProperties(setmeal, setmealDto);

        //查询setmealDishes
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(setmealDishes);//设置setmealDishes

        return setmealDto;
    }

    /**
     * 修改套餐 套餐菜品表
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //更新套餐表信息
        this.updateById(setmealDto);

        //清理当前套餐对应的菜品 setmealDishes表，因为更新操作有可能把之前该套餐下的菜品全删了
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());

        setmealDishService.remove(queryWrapper);

        //更新该套餐下套餐菜品表信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());//设置setmealDishes表的setmealId
            return item;
        }).collect(Collectors.toList());

        //批量插入数据
        setmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<SetmealDto> page(int page, int pageSize, String name) {

        //分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        //添加分页条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        this.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, dtoPage, "records");

        //设置records里面的categoryName
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            //根据分类id查询对象，再查询分类名
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category != null){
                setmealDto.setCategoryName(category.getName());
            }

            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);

        return dtoPage;
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());

        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = this.list(queryWrapper);

        return list;
    }
}
