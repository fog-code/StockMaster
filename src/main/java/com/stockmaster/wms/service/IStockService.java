package com.stockmaster.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stockmaster.wms.entity.Stock;

public interface IStockService extends IService<Stock> {

    /**
     * 🔥 上架操作（使用 FOR UPDATE 防并发）
     * @param auditId  质检记录ID
     * @param kwId     目标库位ID
     * @param qty      上架数量
     */
    void putaway(Long auditId, Long kwId, java.math.BigDecimal qty);
}
