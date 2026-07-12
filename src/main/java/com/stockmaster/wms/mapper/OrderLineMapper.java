package com.stockmaster.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stockmaster.wms.entity.OrderLine;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderLineMapper extends BaseMapper<OrderLine> {}
