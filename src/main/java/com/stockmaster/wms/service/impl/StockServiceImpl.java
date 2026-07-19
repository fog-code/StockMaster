package com.stockmaster.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stockmaster.wms.entity.*;
import com.stockmaster.wms.dto.StockRecommendVO;
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
    private final StockMapper stockMapper;  // 直接注入，用于 FOR UPDATE

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void putaway(Long auditId, Long kwId, BigDecimal qty) {
        // 1. 校验质检记录
        AuditIn audit = auditInService.getById(auditId);
        if (audit == null) throw new RuntimeException("质检记录不存在");
        if (!"已验收".equals(audit.getStatus())) {
            throw new RuntimeException("只有【已验收】的记录可以上架，当前状态：" + audit.getStatus());
        }
        if (audit.getQualifiedQty() == null || audit.getQualifiedQty().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("合格数量为0，无需上架");
        }
        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("上架数量必须大于0");
        }
        if (qty.compareTo(audit.getQualifiedQty()) > 0) {
            throw new RuntimeException(String.format("上架数量(%.2f)不能超过合格数量(%.2f)", qty, audit.getQualifiedQty()));
        }

        // 2. 查库位获取仓库/库区ID
        Kw kw = kwService.getById(kwId);
        if (kw == null) throw new RuntimeException("库位不存在");

        // 3. 🔥 悲观锁：FOR UPDATE 查库存
        Stock stock = stockMapper.selectForUpdate(audit.getGoodsId(), kwId);

        if (stock == null) {
            // 新增库存记录
            stock = new Stock();
            stock.setGoodsId(audit.getGoodsId());
            stock.setKwId(kwId);
            stock.setWarehouseId(kw.getWarehouseId());
            stock.setAreaId(kw.getAreaId());
            stock.setQty(qty);
            this.save(stock);
        } else {
            // 累加库存
            stock.setQty(stock.getQty().add(qty));
            this.updateById(stock);
        }

        // 4. 更新质检记录：标记已上架
        audit.setStatus("已上架");
        audit.setRemark((audit.getRemark() == null ? "" : audit.getRemark() + "; ")
                + "上架库位:" + kw.getKwName() + " x" + qty);
        auditInService.updateById(audit);

        // 5. 检查订单下所有质检是否都已上架 → 订单状态: 已完成
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
        // FIFO：按创建时间升序（最早入库的先用）
        List<Stock> stocks = this.list(new LambdaQueryWrapper<Stock>()
                .eq(Stock::getGoodsId, goodsId)
                .gt(Stock::getQty, 0)
                .orderByAsc(Stock::getCreateTime));

        // 智能推荐：累计库存直到满足需求
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
            BigDecimal pick = s.getQty().min(remaining);  // 取 min(库存, 剩余需求)
            result.add(new StockRecommendVO(
                    s.getId(), s.getGoodsId(), null,
                    s.getKwId(), null, s.getWarehouseId(), null,
                    s.getQty(), pick));
            remaining = remaining.subtract(pick);
        }
        return result;
    }
}
