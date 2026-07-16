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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "入库质检", description = "入库质检记录管理")
@RestController
@RequestMapping("/inbound/audit")
@RequiredArgsConstructor
public class AuditInController {

    private final IAuditInService auditInService;
    private final IOrderHeaderService headerService;
    private final IGoodsService goodsService;

    /**
     * 核心：从入库单生成质检记录
     */
    @PostMapping("/generate/{headerId}")
    @Operation(summary = "从入库单生成质检记录（状态：待验收→验收中）")
    public Result<String> generate(@Parameter(description = "入库单头ID") @PathVariable Long headerId) {
        try {
            auditInService.generateFromOrder(headerId);
            return Result.ok("质检记录已生成，订单状态已变更为【验收中】", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询质检记录
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询质检记录")
    public Result<IPage<AuditIn>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "入库单ID") @RequestParam(required = false) Long headerId,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        LambdaQueryWrapper<AuditIn> w = new LambdaQueryWrapper<>();
        w.eq(headerId != null, AuditIn::getHeaderId, headerId);
        w.eq(StringUtils.hasText(status), AuditIn::getStatus, status);
        w.orderByDesc(AuditIn::getCreateTime);

        IPage<AuditIn> page = auditInService.page(new Page<>(current, size), w);
        fillNames(page.getRecords());
        return Result.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "质检记录详情")
    public Result<AuditIn> detail(@PathVariable Long id) {
        AuditIn a = auditInService.getById(id);
        if (a == null) return Result.error("记录不存在");
        fillNames(List.of(a));
        return Result.ok(a);
    }

    /**
     * 执行质检操作（合格/不合格）
     */
    @PostMapping("/{id}/operate")
    @Operation(summary = "执行质检（合格数量 + 不合格数量）")
    public Result<String> operate(
            @Parameter(description = "质检记录ID") @PathVariable Long id,
            @Parameter(description = "合格数量") @RequestParam java.math.BigDecimal qualifiedQty,
            @Parameter(description = "不合格数量") @RequestParam java.math.BigDecimal unqualifiedQty,
            @Parameter(description = "备注") @RequestParam(required = false) String remark) {
        try {
            auditInService.operate(id, qualifiedQty, unqualifiedQty, remark);
            return Result.ok("质检完成", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 填充关联名称（物料名、订单号）
     */
    private void fillNames(List<AuditIn> audits) {
        if (audits.isEmpty()) return;
        List<Long> goodsIds = audits.stream().map(AuditIn::getGoodsId).distinct().collect(Collectors.toList());
        List<Long> headerIds = audits.stream().map(AuditIn::getHeaderId).distinct().collect(Collectors.toList());

        Map<Long, String> goodsMap = goodsService.listByIds(goodsIds).stream()
                .collect(Collectors.toMap(Goods::getId, Goods::getGoodsName));
        Map<Long, String> orderMap = headerService.listByIds(headerIds).stream()
                .collect(Collectors.toMap(OrderHeader::getId, OrderHeader::getOrderNo));

        audits.forEach(a -> {
            a.setGoodsName(goodsMap.get(a.getGoodsId()));
            a.setOrderNo(orderMap.get(a.getHeaderId()));
        });
    }
}
