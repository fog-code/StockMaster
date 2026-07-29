package com.stockmaster.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stockmaster.wms.entity.Stock;
import com.stockmaster.wms.entity.StockTake;
import com.stockmaster.wms.mapper.StockTakeMapper;
import com.stockmaster.wms.service.IStockService;
import com.stockmaster.wms.service.IStockTakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
        String no = today + "D001";

        List<StockTake> takes = new ArrayList<>();
        for (Stock s : stocks) {
            StockTake t = new StockTake();
            t.setTakeNo(no);
            t.setWarehouseId(s.getWarehouseId());
            t.setAreaId(s.getAreaId());
            t.setGoodsId(s.getGoodsId());
            t.setKwId(s.getKwId());
            t.setBookQty(s.getQty());
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
        String no = today + "L001";

        List<StockTake> takes = new ArrayList<>();
        for (Stock s : stocks) {
            StockTake t = new StockTake();
            t.setTakeNo(no);
            t.setWarehouseId(s.getWarehouseId());
            t.setAreaId(s.getAreaId());
            t.setGoodsId(s.getGoodsId());
            t.setKwId(s.getKwId());
            t.setBookQty(BigDecimal.ZERO);
            t.setTakeType("明盘");
            t.setStatus("待盘点");
            t.setRemark(remark);
            takes.add(t);
        }
        if (!takes.isEmpty()) this.saveBatch(takes);
    }

    @Override
    @Transactional
    public void startTake(Long id) {
        StockTake take = getById(id);
        if (take == null) throw new RuntimeException("盘点单不存在");
        if (!"待盘点".equals(take.getStatus())) throw new RuntimeException("只有待盘点状态的盘点单才能开始");
        take.setStatus("盘点中");
        updateById(take);
    }

    @Override
    @Transactional
    public void fillActual(Long id, BigDecimal actualQty) {
        StockTake take = getById(id);
        if (take == null) throw new RuntimeException("盘点单不存在");
        if (!"盘点中".equals(take.getStatus())) throw new RuntimeException("只有盘点中状态的盘点单才能填写实盘数量");
        if (actualQty == null || actualQty.compareTo(BigDecimal.ZERO) < 0)
            throw new RuntimeException("实盘数量不能为负");

        take.setActualQty(actualQty);
        // 🔥 自动计算差异：实盘 - 账面
        BigDecimal diff = actualQty.subtract(take.getBookQty());
        take.setDiffQty(diff);
        updateById(take);
    }

    @Override
    @Transactional
    public void completeTake(Long id) {
        StockTake take = getById(id);
        if (take == null) throw new RuntimeException("盘点单不存在");
        if (!"盘点中".equals(take.getStatus())) throw new RuntimeException("只有盘点中状态的盘点单才能完成");
        // 确保已填实盘数量
        if (take.getActualQty() == null) throw new RuntimeException("请先填写实盘数量再完成盘点");
        take.setStatus("已完成");
        updateById(take);
    }

    @Override
    public IPage<StockTake> pageDiff(Integer current, Integer size, String takeType) {
        LambdaQueryWrapper<StockTake> w = new LambdaQueryWrapper<>();
        w.ne(StockTake::getDiffQty, BigDecimal.ZERO);  // 只查有差异的
        w.eq(StringUtils.hasText(takeType), StockTake::getTakeType, takeType);
        w.eq(StockTake::getStatus, "已完成");  // 只看已完成的差异
        w.orderByDesc(StockTake::getDiffQty);  // 差异大的排前面
        return this.page(new Page<>(current, size), w);
    }
}
