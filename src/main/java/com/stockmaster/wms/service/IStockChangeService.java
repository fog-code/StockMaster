package com.stockmaster.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stockmaster.wms.entity.StockChange;

public interface IStockChangeService extends IService<StockChange> {
    /** 调整库存数量并记录 */
    void adjustQty(Long stockId, java.math.BigDecimal newQty, String remark);
}
