package com.takeOut.reggie.dto;

import com.takeOut.reggie.entity.Setmeal;
import com.takeOut.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
