package com.stockmaster.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stockmaster.wms.entity.Goods;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
}
