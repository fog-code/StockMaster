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

@Tag(name = "出库下架", description = "出库下架记录管理")
@RestController
@RequestMapping("/outbound/audit")
@RequiredArgsConstructor
public class AuditOutController {

    private final IAuditOutService auditOutService;
    private final IOrderHeaderService headerService;
    private final IGoodsService goodsService;
    private final IKwService kwService;

    @PostMapping("/generate/{headerId}")
    @Operation(summary = "从出库单生成下架记录（含FIFO分配）")
    public Result<String> generate(@PathVariable Long headerId) {
        try {
            auditOutService.generateFromOrder(headerId);
            return Result.ok("下架记录已生成，订单状态已变更为【下架中】", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询下架记录")
    public Result<IPage<AuditOut>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "出库单ID") @RequestParam(required = false) Long headerId,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        LambdaQueryWrapper<AuditOut> w = new LambdaQueryWrapper<>();
        w.eq(headerId != null, AuditOut::getHeaderId, headerId);
        w.eq(StringUtils.hasText(status), AuditOut::getStatus, status);
        w.orderByDesc(AuditOut::getCreateTime);
        IPage<AuditOut> page = auditOutService.page(new Page<>(current, size), w);
        fillNames(page.getRecords());
        return Result.ok(page);
    }

    @PostMapping("/{id}/pickdown")
    @Operation(summary = "🔥 执行下架（FOR UPDATE 扣库存 + 缺货处理）")
    public Result<String> pickdown(@PathVariable Long id) {
        try {
            auditOutService.pickdown(id);
            return Result.ok("下架完成", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    private void fillNames(List<AuditOut> audits) {
        if (audits.isEmpty()) return;
        Map<Long, String> gm = goodsService.listByIds(
                audits.stream().map(AuditOut::getGoodsId).distinct().collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Goods::getId, Goods::getGoodsName));
        Map<Long, String> km = kwService.listByIds(
                audits.stream().map(AuditOut::getKwId).distinct().collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Kw::getId, Kw::getKwName));
        Map<Long, String> om = headerService.listByIds(
                audits.stream().map(AuditOut::getHeaderId).distinct().collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(OrderHeader::getId, OrderHeader::getOrderNo));

        audits.forEach(a -> {
            a.setGoodsName(gm.get(a.getGoodsId()));
            a.setKwName(km.get(a.getKwId()));
            a.setOrderNo(om.get(a.getHeaderId()));
        });
    }
}
