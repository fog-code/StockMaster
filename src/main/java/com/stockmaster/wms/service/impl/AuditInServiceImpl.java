package com.stockmaster.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stockmaster.wms.entity.AuditIn;
import com.stockmaster.wms.entity.OrderHeader;
import com.stockmaster.wms.entity.OrderLine;
import com.stockmaster.wms.mapper.AuditInMapper;
import com.stockmaster.wms.service.IAuditInService;
import com.stockmaster.wms.service.IOrderHeaderService;
import com.stockmaster.wms.service.IOrderLineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditInServiceImpl extends ServiceImpl<AuditInMapper, AuditIn> implements IAuditInService {

    private final IOrderHeaderService headerService;
    private final IOrderLineService lineService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateFromOrder(Long headerId) {
        // 1. 校验订单
        OrderHeader header = headerService.getById(headerId);
        if (header == null) throw new RuntimeException("订单不存在");
        if (!"待验收".equals(header.getStatus())) {
            throw new RuntimeException("只有【待验收】状态的订单可以提交质检，当前状态：" + header.getStatus());
        }

        // 2. 查订单行
        List<OrderLine> lines = lineService.list(
                new LambdaQueryWrapper<OrderLine>().eq(OrderLine::getHeaderId, headerId));

        if (lines.isEmpty()) throw new RuntimeException("订单没有明细");

        // 3. 为每行生成质检记录
        List<AuditIn> audits = new ArrayList<>();
        for (OrderLine line : lines) {
            AuditIn a = new AuditIn();
            a.setHeaderId(headerId);
            a.setLineId(line.getId());
            a.setGoodsId(line.getGoodsId());
            a.setPlanQty(line.getPlanQty());     // 应检数 = 订单计划数
            a.setStatus("待验收");
            audits.add(a);
        }
        this.saveBatch(audits);

        // 4. 更新订单头状态：待验收 → 验收中
        header.setStatus("验收中");
        headerService.updateById(header);

        // 5. 更新所有行状态
        for (OrderLine line : lines) {
            line.setStatus("验收中");
        }
        lineService.updateBatchById(lines);
    }
}
