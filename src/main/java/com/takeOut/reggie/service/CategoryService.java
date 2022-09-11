package com.takeOut.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeOut.reggie.entity.Category;

import java.util.List;

/**
 * @author 小飞侠NO.1
 * @startTime 17:37:28
 */
public interface CategoryService extends IService<Category> {
    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    public void remove(Long id);

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    Page<Category> page(int page, int pageSize);

    /**
     * 根据条件查询分类数据   菜品分类
     * @param category
     * @return
     */
    List<Category> list(Category category);
}
