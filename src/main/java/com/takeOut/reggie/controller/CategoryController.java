package com.takeOut.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeOut.reggie.common.R;
import com.takeOut.reggie.entity.Category;
import com.takeOut.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 小飞侠NO.1
 * @startTime 17:39:04
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("category:{}" + category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        Page<Category> pageInfo = categoryService.page(page, pageSize);

        log.info("pageInfo:{}", pageInfo);
        return R.success(pageInfo);
    }


    /**
     * 根据id删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除分类, id:{}", ids);

        //不能直接删除
//        categoryService.removeById(ids);
        categoryService.remove(ids);

        return R.success("删除成功");
    }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息:{}", category);

        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }


    /**
     * 根据条件查询分类数据   菜品分类
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        log.info("查询分类数据");

        List<Category> list = categoryService.list(category);

        return R.success(list);
    }
}
