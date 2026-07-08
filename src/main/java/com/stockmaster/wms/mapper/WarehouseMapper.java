package com.stockmaster.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stockmaster.wms.entity.Warehouse;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库 Mapper
 */
@Mapper
public interface WarehouseMapper extends BaseMapper<Warehouse> {
}
