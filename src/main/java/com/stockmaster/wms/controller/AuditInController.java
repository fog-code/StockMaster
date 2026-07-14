package com.stockmaster.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stockmaster.common.Result;
import com.stockmaster.wms.entity.*;
import com.stockmaster.wms.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "入库质检管理", description = "从入库单生成质检记录、查询质检列表")
@RestController
@RequestMapping("/inbound/audit")
@RequiredArgsConstructor
public class AuditInController {

    private final IAuditInService auditInService;
    private final IOrderHeaderService orderHeaderService;
    private final IGoodsService goodsService;
    private final IUnitService unitService;

    // ==================== 生成质检记录 ====================

    @PostMapping("/generate/{headerId}")
    @Operation(summary = "从入库单生成质检记录", description = "入库单状态必须为【待验收】，生成后状态变为【验收中】")
    public Result<Integer> generate(@PathVariable Long headerId) {
        try {
            int count = auditInService.generateFromOrder(headerId);
            return Result.ok("成功生成 " + count + " 条质检记录", count);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // ==================== 查询 ====================

    @GetMapping("/page")
    @Operation(summary = "分页查询质检记录（含物料/单位/订单号）")
    public Result<IPage<AuditIn>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "入库单头ID") @RequestParam(required = false) Long headerId,
            @Parameter(description = "物料ID") @RequestParam(required = false) Long goodsId,
            @Parameter(description = "状态：待质检/合格/不合格") @RequestParam(required = false) String status) {

        LambdaQueryWrapper<AuditIn> w = new LambdaQueryWrapper<>();
        w.eq(headerId != null, AuditIn::getHeaderId, headerId);
        w.eq(goodsId != null, AuditIn::getGoodsId, goodsId);
        w.eq(status != null && !status.isEmpty(), AuditIn::getStatus, status);
        w.orderByDesc(AuditIn::getCreateTime);

        IPage<AuditIn> result = auditInService.page(new Page<>(current, size), w);
        if (!result.getRecords().isEmpty()) {
            fillNames(result.getRecords());
        }
        return Result.ok(result);
    }

    @GetMapping("/by-order/{headerId}")
    @Operation(summary = "按入库单ID查询所有质检记录")
    public Result<List<AuditIn>> byOrder(@PathVariable Long headerId) {
        List<AuditIn> list = auditInService.list(
                new LambdaQueryWrapper<AuditIn>()
                        .eq(AuditIn::getHeaderId, headerId)
                        .orderByAsc(AuditIn::getId));
        if (!list.isEmpty()) {
            fillNames(list);
        }
        return Result.ok(list);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询质检记录详情")
    public Result<AuditIn> detail(@PathVariable Long id) {
        AuditIn audit = auditInService.getById(id);
        if (audit == null) return Result.error("质检记录不存在");
        fillNames(List.of(audit));
        return Result.ok(audit);
    }

    // ==================== 辅助 ====================

    private void fillNames(List<AuditIn> list) {
        // 物料
        List<Long> goodsIds = list.stream().map(AuditIn::getGoodsId)
                .filter(g -> g != null).distinct().collect(Collectors.toList());
        Map<Long, Goods> goodsMap = goodsService.listByIds(goodsIds).stream()
                .collect(Collectors.toMap(Goods::getId, g -> g));

        // 单位
        List<Long> unitIds = list.stream().map(AuditIn::getUnitId)
                .filter(u -> u != null).distinct().collect(Collectors.toList());
        Map<Long, String> unitMap = unitService.listByIds(unitIds).stream()
                .collect(Collectors.toMap(Unit::getId, Unit::getUnitName));

        // 入库单号
        List<Long> headerIds = list.stream().map(AuditIn::getHeaderId)
                .filter(h -> h != null).distinct().collect(Collectors.toList());
        Map<Long, String> orderNoMap = orderHeaderService.listByIds(headerIds).stream()
                .collect(Collectors.toMap(OrderHeader::getId, OrderHeader::getOrderNo));

        list.forEach(a -> {
            Goods g = goodsMap.get(a.getGoodsId());
            if (g != null) {
                a.setGoodsName(g.getGoodsName());
                a.setGoodsCode(g.getGoodsCode());
            }
            a.setUnitName(unitMap.get(a.getUnitId()));
            a.setOrderNo(orderNoMap.get(a.getHeaderId()));
        });
    }
}
