package com.stockmaster.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stockmaster.wms.entity.Plat;

public interface IPlatService extends IService<Plat> {
    IPage<Plat> pageWithWarehouse(IPage<Plat> page, Plat condition);
}
