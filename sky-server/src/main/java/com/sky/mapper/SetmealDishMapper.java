package com.sky.mapper;


import com.sky.entity.SetmealDish;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品 id 查询对应的套餐 id
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 保存套餐和菜品的关联关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 删除套餐餐品关系表中的数据
     * @param id
     */
    void deleteBySetmaleId(Long id);

    /**
     * 根据套餐信息查询菜品信息
     * @param id
     * @return
     */
    List<SetmealDish> getBySetmealId(Long id);
}
