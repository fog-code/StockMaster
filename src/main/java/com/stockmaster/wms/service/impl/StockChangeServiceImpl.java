package com.stockmaster.wms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stockmaster.wms.entity.Stock;
import com.stockmaster.wms.entity.StockChange;
import com.stockmaster.wms.mapper.StockChangeMapper;
import com.stockmaster.wms.service.IStockChangeService;
import com.stockmaster.wms.service.IStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class StockChangeServiceImpl extends ServiceImpl<StockChangeMapper, StockChange>
        implements IStockChangeService {

    private final IStockService stockService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustQty(Long stockId, BigDecimal newQty, String remark) {
        Stock stock = stockService.getById(stockId);
        if (stock == null) throw new RuntimeException("库存记录不存在");
        if (newQty == null || newQty.compareTo(BigDecimal.ZERO) < 0)
            throw new RuntimeException("数量不能为负数");

        BigDecimal before = stock.getQty() != null ? stock.getQty() : BigDecimal.ZERO;
        // 记录调整
        StockChange change = new StockChange();
        change.setStockId(stockId);
        change.setGoodsId(stock.getGoodsId());
        change.setKwId(stock.getKwId());
        change.setChangeType("数量调整");
        change.setBeforeQty(before);
        change.setAfterQty(newQty);
        change.setChangeQty(newQty.subtract(before));
        change.setRemark(remark);
        this.save(change);

        // 更新库存
        stock.setQty(newQty);
        stockService.updateById(stock);
    }
}
