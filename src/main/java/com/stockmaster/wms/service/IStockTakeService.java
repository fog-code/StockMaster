package com.stockmaster.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stockmaster.wms.entity.StockTake;

public interface IStockTakeService extends IService<StockTake> {
    /** 创建暗盘（自动获取账面库存） */
    void createDarkTake(Long warehouseId);
    /** 创建明盘 */
    void createLightTake(Long warehouseId, String remark);
}
