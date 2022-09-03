package com.takeOut.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeOut.reggie.entity.Category;

/**
 * @author 小飞侠NO.1
 * @startTime 17:37:28
 */
public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
