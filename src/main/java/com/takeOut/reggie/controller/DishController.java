package com.takeOut.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeOut.reggie.common.R;
import com.takeOut.reggie.dto.DishDto;
import com.takeOut.reggie.entity.Dish;
import com.takeOut.reggie.service.CategoryService;
import com.takeOut.reggie.service.DishFlavorService;
import com.takeOut.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 菜品管理     菜品 菜品口味相关操作统一放在这里 DishController
 * @author 小飞侠NO.1
 * @startTime 16:09:33
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "dishPage", allEntries = true)
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        //清理所有菜品的缓存数据(全部)
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);


        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
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
    @GetMapping("/page")
    @Cacheable(value = "dishPage", key = "'p_' + #page + '&pS_' + #pageSize + '&n_' + #name")
    public R<Page> page(int page, int pageSize, String name){
        log.info("菜品信息分页查询,page:{}, pageSize:{}, name:{}", page, pageSize, name);

        Page<DishDto> dishDtoPage = dishService.page(page, pageSize, name);

        return R.success(dishDtoPage);
    }


    /**
     * 根据id信息查询对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        log.info("据id:{}信息查询对应的口味信息", id);

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }


    /**
     * 更新菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    @CacheEvict(value = "dishPage", allEntries = true)
    public R<String> update(@RequestBody DishDto dishDto){
        log.info("更新菜品:{}", dishDto.toString());

        //清理所有菜品的缓存数据(全部)
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);

        //清理某个分类下面的菜品缓存数据(精准)
        String key = "dish_" + dishDto.getCategoryId() + dishDto.getStatus();
        redisTemplate.delete(key);

        dishService.updateWithFlavor(dishDto);

        return R.success("更新菜品成功");
    }


    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus, 1);//添加状态为1(起售状态)的菜品
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }

    /**
     * 根据条件查询对应的菜品数据    DishDto在Dish原来的基础上添加了flavors和categoryName
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        log.info("根据dish条件查询菜品");

        List<DishDto> dishDtoList ;
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();//dish_1397844263642378242_1

        //先从Redis缓存中获取数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if(dishDtoList != null) {
            //如果存在，直接返回，无需查询数据库
            return R.success(dishDtoList);
        }

        //查询数据库
        dishDtoList = dishService.list(dish);

        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到Redis中
        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "dishPage", allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("通过ids：{}删除", ids);
        //清理所有菜品的缓存数据(全部)
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);


        boolean flag = dishService.remove(ids);
        return flag == true ? R.success("删除成功") : R.error("删除失败");
    }

    /**
     * 改变状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    @CacheEvict(value = "dishPage", allEntries = true)
    public R<String> changeStatus(@PathVariable("status") Integer status, @RequestParam List<Long> ids){
        log.info("菜品停售或者起售");
        //清理所有菜品的缓存数据(全部)
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);


        boolean flag = dishService.changeStatus(status, ids);
        return flag == true ? R.success("菜品改变状态成功") : R.error("菜品改变状态失败");
    }

}
