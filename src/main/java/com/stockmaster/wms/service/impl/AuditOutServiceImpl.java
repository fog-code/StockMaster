package com.stockmaster.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stockmaster.wms.dto.StockRecommendVO;
import com.stockmaster.wms.entity.*;
import com.stockmaster.wms.mapper.AuditOutMapper;
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
public class AuditOutServiceImpl extends ServiceImpl<AuditOutMapper, AuditOut> implements IAuditOutService {

    private final IOrderHeaderService headerService;
    private final IOrderLineService lineService;
    private final IStockService stockService;
    private final StockMapper stockMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateFromOrder(Long headerId) {
        // 1. 校验
        OrderHeader header = headerService.getById(headerId);
        if (header == null) throw new RuntimeException("订单不存在");
        if (!"待验收".equals(header.getStatus()))
            throw new RuntimeException("只有【待验收】状态可提交下架");

        List<OrderLine> lines = lineService.list(
                new LambdaQueryWrapper<OrderLine>().eq(OrderLine::getHeaderId, headerId));
        if (lines.isEmpty()) throw new RuntimeException("订单没有明细");

        // 2. 为每行生成下架记录（含FIFO分配）
        List<AuditOut> audits = new ArrayList<>();
        for (OrderLine line : lines) {
            List<StockRecommendVO> vos = stockService.recommendStockDetail(
                    line.getGoodsId(), line.getPlanQty());
            for (StockRecommendVO vo : vos) {
                AuditOut a = new AuditOut();
                a.setHeaderId(headerId);
                a.setLineId(line.getId());
                a.setGoodsId(line.getGoodsId());
                a.setStockId(vo.getStockId());
                a.setKwId(vo.getKwId());
                a.setPlanQty(vo.getPickQty());
                a.setStatus("待下架");
                audits.add(a);
            }
        }

        if (audits.isEmpty()) throw new RuntimeException("库存不足，无法生成下架记录");
        this.saveBatch(audits);

        // 3. 更新订单状态：待验收 → 下架中
        header.setStatus("下架中");
        headerService.updateById(header);
        lines.forEach(l -> { l.setStatus("下架中"); lineService.updateById(l); });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pickdown(Long auditOutId) {
        AuditOut audit = this.getById(auditOutId);
        if (audit == null) throw new RuntimeException("下架记录不存在");
        if (!"待下架".equals(audit.getStatus()))
            throw new RuntimeException("只有【待下架】状态可下架，当前：" + audit.getStatus());

        // 🔥 FOR UPDATE
        Stock stock = stockMapper.selectForUpdate(audit.getGoodsId(), audit.getKwId());
        if (stock == null) {
            audit.setRealQty(BigDecimal.ZERO);
            audit.setStatus("缺货");
            audit.setRemark("库存不足：目标库位无库存");
            this.updateById(audit);
        } else {
            BigDecimal need = audit.getPlanQty();
            if (stock.getQty().compareTo(need) >= 0) {
                stock.setQty(stock.getQty().subtract(need));
                stockService.updateById(stock);
                audit.setRealQty(need);
                audit.setStatus("已下架");
            } else {
                audit.setRealQty(stock.getQty());
                audit.setStatus("缺货");
                audit.setRemark(String.format("库存不足：需%.2f，实%.2f，缺%.2f",
                        need, stock.getQty(), need.subtract(stock.getQty())));
                stock.setQty(BigDecimal.ZERO);
                stockService.updateById(stock);
            }
            this.updateById(audit);
        }

        // 订单完成检查
        long pending = this.count(new LambdaQueryWrapper<AuditOut>()
                .eq(AuditOut::getHeaderId, audit.getHeaderId())
                .eq(AuditOut::getStatus, "待下架"));
        if (pending == 0) {
            OrderHeader h = headerService.getById(audit.getHeaderId());
            h.setStatus("已完成");
            headerService.updateById(h);
            lineService.list(new LambdaQueryWrapper<OrderLine>()
                    .eq(OrderLine::getHeaderId, audit.getHeaderId()))
                    .forEach(l -> { l.setStatus("已完成"); lineService.updateById(l); });
        }
    }
}
