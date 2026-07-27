package com.stockmaster.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stockmaster.wms.entity.Stock;
import com.stockmaster.wms.entity.StockTake;
import com.stockmaster.wms.mapper.StockTakeMapper;
import com.stockmaster.wms.service.IStockService;
import com.stockmaster.wms.service.IStockTakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockTakeServiceImpl extends ServiceImpl<StockTakeMapper, StockTake> implements IStockTakeService {

    private final IStockService stockService;

    @Override
    @Transactional
    public void createDarkTake(Long warehouseId) {
        List<Stock> stocks = stockService.list(new LambdaQueryWrapper<Stock>()
                .eq(warehouseId != null, Stock::getWarehouseId, warehouseId));
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String no = today + "D001";  // D=暗盘

        List<StockTake> takes = new ArrayList<>();
        for (Stock s : stocks) {
            StockTake t = new StockTake();
            t.setTakeNo(no);
            t.setWarehouseId(s.getWarehouseId());
            t.setAreaId(s.getAreaId());
            t.setGoodsId(s.getGoodsId());
            t.setKwId(s.getKwId());
            t.setBookQty(s.getQty());  // 🔥 暗盘：自动获取账面库存
            t.setTakeType("暗盘");
            t.setStatus("待盘点");
            takes.add(t);
        }
        if (!takes.isEmpty()) this.saveBatch(takes);
    }

    @Override
    @Transactional
    public void createLightTake(Long warehouseId, String remark) {
        List<Stock> stocks = stockService.list(new LambdaQueryWrapper<Stock>()
                .eq(warehouseId != null, Stock::getWarehouseId, warehouseId));
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String no = today + "L001";  // L=明盘

        List<StockTake> takes = new ArrayList<>();
        for (Stock s : stocks) {
            StockTake t = new StockTake();
            t.setTakeNo(no);
            t.setWarehouseId(s.getWarehouseId());
            t.setAreaId(s.getAreaId());
            t.setGoodsId(s.getGoodsId());
            t.setKwId(s.getKwId());
            t.setBookQty(BigDecimal.ZERO);  // 明盘：账面填0，人工填实盘
            t.setTakeType("明盘");
            t.setStatus("待盘点");
            t.setRemark(remark);
            takes.add(t);
        }
        if (!takes.isEmpty()) this.saveBatch(takes);
    }
}
