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

    /**
     * 🔥 FIFO 库存推荐：按库位创建时间推荐可用库存
     * @param goodsId 物料ID
     * @param qty     需求数量
     * @return 推荐的库存记录列表（按创建时间升序=先进先出）
     */
    java.util.List<Stock> recommendStock(Long goodsId, java.math.BigDecimal qty);
}
