package com.stockmaster.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stockmaster.wms.dto.StockRecommendVO;
import com.stockmaster.wms.entity.*;
import com.stockmaster.wms.mapper.StockMapper;
import com.stockmaster.wms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements IStockService {

    private final IAuditInService auditInService;
    private final IKwService kwService;
    private final IOrderHeaderService headerService;
    private final IOrderLineService lineService;
    private final StockMapper stockMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void putaway(Long auditId, Long kwId, BigDecimal qty) {
        AuditIn audit = auditInService.getById(auditId);
        if (audit == null) throw new RuntimeException("质检记录不存在");
        if (!"已验收".equals(audit.getStatus()))
            throw new RuntimeException("只有【已验收】的记录可以上架，当前状态：" + audit.getStatus());
        if (audit.getQualifiedQty() == null || audit.getQualifiedQty().compareTo(BigDecimal.ZERO) <= 0)
            throw new RuntimeException("合格数量为0，无需上架");
        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0)
            throw new RuntimeException("上架数量必须大于0");
        if (qty.compareTo(audit.getQualifiedQty()) > 0)
            throw new RuntimeException(String.format("上架数量(%.2f)不能超过合格数量(%.2f)", qty, audit.getQualifiedQty()));

        Kw kw = kwService.getById(kwId);
        if (kw == null) throw new RuntimeException("库位不存在");

        Stock stock = stockMapper.selectForUpdate(audit.getGoodsId(), kwId);
        if (stock == null) {
            stock = new Stock();
            stock.setGoodsId(audit.getGoodsId());
            stock.setKwId(kwId);
            stock.setWarehouseId(kw.getWarehouseId());
            stock.setAreaId(kw.getAreaId());
            stock.setQty(qty);
            this.save(stock);
        } else {
            stock.setQty(stock.getQty().add(qty));
            this.updateById(stock);
        }

        audit.setStatus("已上架");
        audit.setRemark((audit.getRemark() == null ? "" : audit.getRemark() + "; ")
                + "上架库位:" + kw.getKwName() + " x" + qty);
        auditInService.updateById(audit);

        long pending = auditInService.count(new LambdaQueryWrapper<AuditIn>()
                .eq(AuditIn::getHeaderId, audit.getHeaderId())
                .ne(AuditIn::getStatus, "已上架"));
        if (pending == 0) {
            OrderHeader header = headerService.getById(audit.getHeaderId());
            header.setStatus("已完成");
            headerService.updateById(header);
            lineService.list(new LambdaQueryWrapper<OrderLine>()
                    .eq(OrderLine::getHeaderId, audit.getHeaderId()))
                    .forEach(l -> { l.setStatus("已完成"); lineService.updateById(l); });
        }
    }

    @Override
    public List<Stock> recommendStock(Long goodsId, BigDecimal qty) {
        List<Stock> stocks = this.list(new LambdaQueryWrapper<Stock>()
                .eq(Stock::getGoodsId, goodsId)
                .gt(Stock::getQty, 0)
                .orderByAsc(Stock::getCreateTime));
        List<Stock> result = new ArrayList<>();
        BigDecimal accumulated = BigDecimal.ZERO;
        for (Stock s : stocks) {
            result.add(s);
            accumulated = accumulated.add(s.getQty());
            if (accumulated.compareTo(qty) >= 0) break;
        }
        return result;
    }

    @Override
    public List<StockRecommendVO> recommendStockDetail(Long goodsId, BigDecimal qty) {
        List<Stock> stocks = this.list(new LambdaQueryWrapper<Stock>()
                .eq(Stock::getGoodsId, goodsId)
                .gt(Stock::getQty, 0)
                .orderByAsc(Stock::getCreateTime));
        List<StockRecommendVO> result = new ArrayList<>();
        BigDecimal remaining = qty;
        for (Stock s : stocks) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
            BigDecimal pick = s.getQty().min(remaining);
            result.add(new StockRecommendVO(
                    s.getId(), s.getGoodsId(), null,
                    s.getKwId(), null, s.getWarehouseId(), null,
                    s.getQty(), pick));
            remaining = remaining.subtract(pick);
        }
        return result;
    }
}
