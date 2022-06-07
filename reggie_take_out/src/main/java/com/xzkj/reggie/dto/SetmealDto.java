package com.xzkj.reggie.dto;

import com.xzkj.reggie.entity.Setmeal;
import com.xzkj.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
