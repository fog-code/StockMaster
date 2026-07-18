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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "出库单管理", description = "出库单主子表 CRUD + FIFO 库存推荐")
@RestController
@RequestMapping("/outbound/order")
@RequiredArgsConstructor
public class OutboundOrderController {

    private final IOrderHeaderService headerService;
    private final IOrderLineService lineService;
    private final IWarehouseService warehouseService;
    private final ICusService cusService;
    private final IOrderTypeService orderTypeService;
    private final IGoodsService goodsService;
    private final IUnitService unitService;
    private final IStockService stockService;

    // ==================== 表单选项 ====================

    @GetMapping("/form-options")
    @Operation(summary = "获取出库单表单下拉数据")
    public Result<Map<String, Object>> formOptions() {
        return Result.ok(Map.of(
            "warehouses", warehouseService.list(),
            "customers", cusService.list(),
            "orderTypes", orderTypeService.list(
                new LambdaQueryWrapper<OrderType>().eq(OrderType::getDirection, "OUT")),
            "goods", goodsService.list(),
            "units", unitService.list()
        ));
    }

    // ==================== 🔥 FIFO 库存推荐 ====================

    @GetMapping("/stock-recommend")
    @Operation(summary = "FIFO 库存推荐（按库位创建时间，先进先出）")
    public Result<List<Stock>> recommendStock(
            @Parameter(description = "物料ID") @RequestParam Long goodsId,
            @Parameter(description = "需求数量") @RequestParam BigDecimal qty) {
        List<Stock> stocks = stockService.recommendStock(goodsId, qty);
        // 填关联名
        if (!stocks.isEmpty()) {
            Map<Long, String> gm = goodsService.listByIds(
                    stocks.stream().map(Stock::getGoodsId).distinct().collect(Collectors.toList()))
                    .stream().collect(Collectors.toMap(Goods::getId, Goods::getGoodsName));
            Map<Long, String> km = new java.util.HashMap<>();
            Map<Long, String> wm = new java.util.HashMap<>();
            stocks.forEach(s -> {
                s.setGoodsName(gm.get(s.getGoodsId()));
                s.setKwName(km.computeIfAbsent(s.getKwId(),
                    kid -> { Kw k = new Kw(); k.setKwName("库位"+kid); return k.getKwName(); }));
            });
        }
        BigDecimal total = stocks.stream().map(Stock::getQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        return Result.ok("推荐库存总量: " + total, stocks);
    }

    // ==================== 查询 ====================

    @GetMapping("/page")
    @Operation(summary = "分页查询出库单")
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
        if (!page.getRecords().isEmpty()) fillNames(page.getRecords());
        return Result.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询出库单详情（含子表）")
    public Result<OrderHeader> detail(@PathVariable Long id) {
        OrderHeader h = headerService.getById(id);
        if (h == null) return Result.error("订单不存在");
        List<OrderLine> lines = lineService.list(
                new LambdaQueryWrapper<OrderLine>().eq(OrderLine::getHeaderId, id));
        if (!lines.isEmpty()) {
            Map<Long, Goods> gm = goodsService.listByIds(
                    lines.stream().map(OrderLine::getGoodsId).filter(gid -> gid != null).collect(Collectors.toList()))
                    .stream().collect(Collectors.toMap(Goods::getId, g -> g));
            lines.forEach(l -> { Goods g = gm.get(l.getGoodsId()); if (g != null) l.setGoodsName(g.getGoodsName()); });
        }
        h.setLines(lines);
        fillNames(List.of(h));
        return Result.ok(h);
    }

    // ==================== 新增（supplyId 存客户ID） ====================

    @PostMapping
    @Operation(summary = "创建出库单（supplyId 改为存客户ID）")
    public Result<OrderHeader> create(@RequestBody OrderHeader header) {
        if (header.getLines() == null || header.getLines().isEmpty())
            return Result.error("请至少添加一条出库明细");
        headerService.saveWithLines(header);
        return detail(header.getId());
    }

    // ==================== 修改 ====================
    @PutMapping
    @Operation(summary = "修改出库单（仅待验收可改）")
    public Result<OrderHeader> update(@RequestBody OrderHeader header) {
        if (header.getId() == null) return Result.error("ID不能为空");
        OrderHeader db = headerService.getById(header.getId());
        if (db == null) return Result.error("订单不存在");
        if (!"待验收".equals(db.getStatus()))
            return Result.error("只有【待验收】状态可修改");
        headerService.updateById(header);
        if (header.getLines() != null) {
            lineService.remove(new LambdaQueryWrapper<OrderLine>().eq(OrderLine::getHeaderId, header.getId()));
            header.getLines().forEach(l -> { l.setId(null); l.setHeaderId(header.getId()); });
            if (!header.getLines().isEmpty()) lineService.saveBatch(header.getLines());
        }
        return detail(header.getId());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除出库单（仅待验收可删）")
    public Result<Void> delete(@PathVariable Long id) {
        OrderHeader db = headerService.getById(id);
        if (db == null) return Result.error("订单不存在");
        if (!"待验收".equals(db.getStatus()))
            return Result.error("只有【待验收】状态可删除");
        lineService.remove(new LambdaQueryWrapper<OrderLine>().eq(OrderLine::getHeaderId, id));
        headerService.removeById(id);
        return Result.ok("删除成功", null);
    }

    private void fillNames(List<OrderHeader> headers) {
        List<Long> whIds = headers.stream().map(OrderHeader::getWarehouseId).distinct().collect(Collectors.toList());
        List<Long> spIds = headers.stream().map(OrderHeader::getSupplyId).distinct().collect(Collectors.toList());
        Map<Long, String> whMap = warehouseService.listByIds(whIds).stream()
                .collect(Collectors.toMap(Warehouse::getId, Warehouse::getWarehouseName));
        Map<Long, String> cusMap = cusService.listByIds(spIds).stream()
                .collect(Collectors.toMap(Cus::getId, Cus::getCusName));
        headers.forEach(h -> {
            h.setWarehouseName(whMap.get(h.getWarehouseId()));
            h.setSupplyName(cusMap.get(h.getSupplyId()));  // supplyName 当客户名显示
        });
    }
}
