package com.stockmaster.wms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stockmaster.wms.entity.Warehouse;
import com.stockmaster.wms.mapper.WarehouseMapper;
import com.stockmaster.wms.service.IWarehouseService;
import org.springframework.stereotype.Service;

/**
 * 仓库 Service 实现
 */
@Service
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, Warehouse>
        implements IWarehouseService {
}
