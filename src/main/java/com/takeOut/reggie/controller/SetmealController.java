package com.takeOut.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeOut.reggie.common.R;
import com.takeOut.reggie.dto.SetmealDto;
import com.takeOut.reggie.entity.Setmeal;
import com.takeOut.reggie.service.CategoryService;
import com.takeOut.reggie.service.SetmealDishService;
import com.takeOut.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author 小飞侠NO.1
 * @startTime 18:28:29
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息:{}", setmealDto.toString());
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }


    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("套餐分页查询");

        Page<SetmealDto> dtoPage = setmealService.page(page, pageSize, name);

        return R.success(dtoPage);
    }


    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> delete(@RequestParam(value = "ids") List<Long> ids){
        log.info("ids:{}", ids);

        if(ids.size() == 0){
            return R.error("小哥哥/小姐姐，未选中，不能给删除哦-_-");
        }

        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    //需要R序列化，实现Serializable接口
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        log.info("根据条件查询套餐数据");

        List<Setmeal> list = setmealService.list(setmeal);

        return R.success(list);
    }

    /**
     * 菜品停售或者起售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable Integer status, @RequestParam List<Long> ids){
        log.info("c");
        boolean flag = setmealService.changeStatus(status, ids);
        return flag == true ? R.success("套餐状态改变成功") : R.error("套餐状态改变失败");
    }

    /**
     * 根据setmealId查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info("更新套餐:{}", setmealDto.toString());

        setmealService.updateWithDish(setmealDto);

        return R.success("更新套餐成功");
    }
}
