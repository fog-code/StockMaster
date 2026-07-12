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

@Tag(name = "入库单管理", description = "入库单主子表 CRUD")
@RestController
@RequestMapping("/inbound/order")
@RequiredArgsConstructor
public class InboundOrderController {

    private final IOrderHeaderService headerService;
    private final IOrderLineService lineService;
    private final IWarehouseService warehouseService;
    private final ISupplyService supplyService;
    private final IOrderTypeService orderTypeService;
    private final IGoodsService goodsService;

    // ==================== 查询 ====================

    @GetMapping("/page")
    @Operation(summary = "分页查询入库单（含仓库/供应商/类型名）")
    public Result<IPage<OrderHeader>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "订单号") @RequestParam(required = false) String orderNo,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        LambdaQueryWrapper<OrderHeader> w = new LambdaQueryWrapper<>();
        w.like(StringUtils.hasText(orderNo), OrderHeader::getOrderNo, orderNo);
        w.eq(StringUtils.hasText(status), OrderHeader::getStatus, status);
        w.orderByDesc(OrderHeader::getCreateTime);
        IPage<OrderHeader> page = headerService.page(new Page<>(current, size), w);

        if (!page.getRecords().isEmpty()) {
            fillNames(page.getRecords());
        }
        return Result.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询入库单详情（含子表）")
    public Result<OrderHeader> detail(@PathVariable Long id) {
        OrderHeader header = headerService.getById(id);
        if (header == null) return Result.error("订单不存在");

        // 查子表
        List<OrderLine> lines = lineService.list(
                new LambdaQueryWrapper<OrderLine>().eq(OrderLine::getHeaderId, id));
        // 填物料名
        if (!lines.isEmpty()) {
            Map<Long, Goods> goodsMap = goodsService.listByIds(
                    lines.stream().map(OrderLine::getGoodsId).filter(gid -> gid != null).collect(Collectors.toList()))
                    .stream().collect(Collectors.toMap(Goods::getId, g -> g));
            lines.forEach(l -> {
                Goods g = goodsMap.get(l.getGoodsId());
                if (g != null) { l.setGoodsName(g.getGoodsName()); l.setGoodsCode(g.getGoodsCode()); }
            });
        }
        header.setLines(lines);
        fillNames(List.of(header));
        return Result.ok(header);
    }

    // ==================== 新增（主子表联动） ====================

    @PostMapping
    @Operation(summary = "创建入库单（含子表明细）")
    public Result<OrderHeader> create(@RequestBody OrderHeader header) {
        if (header.getLines() == null || header.getLines().isEmpty()) {
            return Result.error("请至少添加一条入库明细");
        }
        headerService.saveWithLines(header);
        // 重新查一次带关联名
        return detail(header.getId());
    }

    // ==================== 修改 ====================

    @PutMapping
    @Operation(summary = "修改入库单（仅待验收状态可改）")
    public Result<OrderHeader> update(@RequestBody OrderHeader header) {
        if (header.getId() == null) return Result.error("ID不能为空");

        OrderHeader db = headerService.getById(header.getId());
        if (db == null) return Result.error("订单不存在");
        if (!"待验收".equals(db.getStatus())) {
            return Result.error("只有【待验收】状态的订单可以修改");
        }

        // 更新头
        headerService.updateById(header);

        // 更新子表：先删后插
        if (header.getLines() != null) {
            lineService.remove(new LambdaQueryWrapper<OrderLine>().eq(OrderLine::getHeaderId, header.getId()));
            header.getLines().forEach(l -> { l.setId(null); l.setHeaderId(header.getId()); });
            if (!header.getLines().isEmpty()) {
                lineService.saveBatch(header.getLines());
            }
        }
        return detail(header.getId());
    }

    // ==================== 删除 ====================

    @DeleteMapping("/{id}")
    @Operation(summary = "删除入库单（仅待验收状态可删）")
    public Result<Void> delete(@PathVariable Long id) {
        OrderHeader db = headerService.getById(id);
        if (db == null) return Result.error("订单不存在");
        if (!"待验收".equals(db.getStatus())) {
            return Result.error("只有【待验收】状态的订单可以删除");
        }
        // 删子表
        lineService.remove(new LambdaQueryWrapper<OrderLine>().eq(OrderLine::getHeaderId, id));
        // 删头
        headerService.removeById(id);
        return Result.ok("删除成功", null);
    }

    // ==================== 辅助 ====================

    private void fillNames(List<OrderHeader> headers) {
        List<Long> whIds = headers.stream().map(OrderHeader::getWarehouseId).distinct().collect(Collectors.toList());
        List<Long> spIds = headers.stream().map(OrderHeader::getSupplyId).distinct().collect(Collectors.toList());
        List<Long> otIds = headers.stream().map(OrderHeader::getOrderTypeId).distinct().collect(Collectors.toList());

        Map<Long, String> whMap = warehouseService.listByIds(whIds).stream()
                .collect(Collectors.toMap(Warehouse::getId, Warehouse::getWarehouseName));
        Map<Long, String> spMap = supplyService.listByIds(spIds).stream()
                .collect(Collectors.toMap(Supply::getId, Supply::getSupplyName));
        Map<Long, String> otMap = orderTypeService.listByIds(otIds).stream()
                .collect(Collectors.toMap(OrderType::getId, OrderType::getTypeName));

        headers.forEach(h -> {
            h.setWarehouseName(whMap.get(h.getWarehouseId()));
            h.setSupplyName(spMap.get(h.getSupplyId()));
            h.setOrderTypeName(otMap.get(h.getOrderTypeId()));
        });
    }
}
