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

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditInServiceImpl extends ServiceImpl<AuditInMapper, AuditIn>
        implements IAuditInService {

    private final IOrderHeaderService orderHeaderService;
    private final IOrderLineService orderLineService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateFromOrder(Long headerId) {
        // 1. 校验入库单存在且状态为"待验收"
        OrderHeader header = orderHeaderService.getById(headerId);
        if (header == null) {
            throw new RuntimeException("入库单不存在");
        }
        if (!"待验收".equals(header.getStatus())) {
            throw new RuntimeException("只有【待验收】状态的入库单可以生成质检记录");
        }

        // 2. 查出所有订单行
        List<OrderLine> lines = orderLineService.list(
                new LambdaQueryWrapper<OrderLine>().eq(OrderLine::getHeaderId, headerId));
        if (lines.isEmpty()) {
            throw new RuntimeException("入库单没有明细行，无法生成质检记录");
        }

        // 3. 检查是否已经生成过质检记录（防止重复生成）
        long existCount = this.count(
                new LambdaQueryWrapper<AuditIn>().eq(AuditIn::getHeaderId, headerId));
        if (existCount > 0) {
            throw new RuntimeException("该入库单已生成过质检记录，请勿重复操作");
        }

        // 4. 为每条订单行创建质检记录
        List<AuditIn> auditList = lines.stream().map(line -> {
            AuditIn audit = new AuditIn();
            audit.setHeaderId(headerId);
            audit.setLineId(line.getId());
            audit.setGoodsId(line.getGoodsId());
            audit.setUnitId(line.getUnitId());
            audit.setPlanQty(line.getPlanQty());
            audit.setStatus("待质检");
            return audit;
        }).toList();

        this.saveBatch(auditList);

        // 5. 更新入库单状态为"验收中"
        header.setStatus("验收中");
        orderHeaderService.updateById(header);

        return auditList.size();
    }
}
