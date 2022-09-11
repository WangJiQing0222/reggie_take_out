package com.takeOut.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeOut.reggie.common.CustomeException;
import com.takeOut.reggie.dto.DishDto;
import com.takeOut.reggie.entity.Category;
import com.takeOut.reggie.entity.Dish;
import com.takeOut.reggie.entity.DishFlavor;
import com.takeOut.reggie.mapper.DishMapper;
import com.takeOut.reggie.service.CategoryService;
import com.takeOut.reggie.service.DishFlavorService;
import com.takeOut.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 小飞侠NO.1
 * @startTime 19:16:44
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品，同时保存对应的口味表 dish dish_flavor
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId();//菜品口味表dish_flavor里面的 菜品id

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish, dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 更新菜品
     * @param dishDto
     * @return
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据--dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据--dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());//必须要设置
            return item;
        }).collect(Collectors.toList());



        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品(停售) 和对应的口味
     * @param ids
     * @return
     */
    @Override
    public boolean remove(List<Long> ids) {
        List<Dish> dishes = super.listByIds(ids);
        for(Dish dish : dishes){
            if(dish.getStatus() == 1) {
                // 如果状态为1(起售) 删除失败
                throw new CustomeException("删除失败，删除的条目中含有未停售的菜品");
            }
        }

        //删除对应的菜品表数据
        boolean flag1 = super.removeByIds(ids);

        //删除对应的口味表数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId, ids);
        boolean flag2 = dishFlavorService.remove(queryWrapper);

        return flag1 && flag2;
    }

    /**
     * 改变状态
     * @param status
     * @param ids
     * @return
     */
    @Override
    public boolean changeStatus(Integer status, List<Long> ids) {
        //停售
        if(status == 0){
            List<Dish> dishes = super.listByIds(ids);
            if (dishes != null){
                for(Dish dish : dishes)
                    dish.setStatus(0);//改变状态为停售

                updateBatchById(dishes);
                return true;
            }
        }else {     //起售
            List<Dish> dishes = super.listByIds(ids);
            if (dishes != null){
                for(Dish dish : dishes)
                    dish.setStatus(1);//改变状态为起售

                updateBatchById(dishes);
                return true;
            }
        }
        return false;
    }

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
    @Override
    public Page<DishDto> page(int page, int pageSize, String name) {
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        this.page(pageInfo, queryWrapper);


        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        //records里面有categoryId
        List<Dish> records = pageInfo.getRecords();

        //根据categoryId查询Category记录，设置categoryName
        List<DishDto> list = records.stream().map((item) -> {
            //
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            //
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category != null){
                //DishDto里面有categoryName,设置它
                dishDto.setCategoryName(category.getName());
            }

            return dishDto;
        }).collect(Collectors.toList());

        //
        dishDtoPage.setRecords(list);

        return dishDtoPage;
    }

    /**
     * 根据条件查询对应的菜品数据    DishDto在Dish原来的基础上添加了flavors和categoryName
     * @param dish
     * @return
     */
    @Override
    public List<DishDto> list(Dish dish) {
        List<DishDto> dishDtoList;


        //查询条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);//添加状态为1(起售状态)的菜品
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分类id

            Category category = categoryService.getById(categoryId);
            if(category != null){
                dishDto.setCategoryName(category.getName());//设置categoryName
            }

            //当前菜品id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);

            dishDto.setFlavors(dishFlavorList);//设置flavors
            return dishDto;
        }).collect(Collectors.toList());


        return dishDtoList;
    }

}
