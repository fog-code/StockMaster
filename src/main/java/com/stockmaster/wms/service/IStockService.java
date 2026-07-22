package com.stockmaster.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stockmaster.wms.entity.Stock;

public interface IStockService extends IService<Stock> {

    void putaway(Long auditId, Long kwId, java.math.BigDecimal qty);

    java.util.List<Stock> recommendStock(Long goodsId, java.math.BigDecimal qty);

    java.util.List<com.stockmaster.wms.dto.StockRecommendVO> recommendStockDetail(Long goodsId, java.math.BigDecimal qty);
}
