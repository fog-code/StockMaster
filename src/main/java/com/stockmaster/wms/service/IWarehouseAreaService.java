package com.stockmaster.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stockmaster.wms.entity.WarehouseArea;

public interface IWarehouseAreaService extends IService<WarehouseArea> {
    IPage<WarehouseArea> pageWithWarehouse(IPage<WarehouseArea> page, WarehouseArea condition);
}
