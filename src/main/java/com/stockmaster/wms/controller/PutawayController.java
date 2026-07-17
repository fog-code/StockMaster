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

@Tag(name = "上架管理", description = "上架操作 + 库存查询")
@RestController
@RequestMapping("/inbound")
@RequiredArgsConstructor
public class PutawayController {

    private final IStockService stockService;
    private final IGoodsService goodsService;
    private final IKwService kwService;
    private final IWarehouseService warehouseService;

    /**
     * 🔥 上架操作（FOR UPDATE 锁）
     */
    @PostMapping("/putaway")
    @Operation(summary = "执行上架（悲观锁防并发，库存新增/累加）")
    public Result<String> putaway(
            @Parameter(description = "质检记录ID") @RequestParam Long auditId,
            @Parameter(description = "目标库位ID") @RequestParam Long kwId,
            @Parameter(description = "上架数量") @RequestParam BigDecimal qty) {
        try {
            stockService.putaway(auditId, kwId, qty);
            return Result.ok("上架成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 库存查询
     */
    @GetMapping("/stock/page")
    @Operation(summary = "库存分页查询")
    public Result<IPage<Stock>> stockPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "物料名称") @RequestParam(required = false) String goodsName,
            @Parameter(description = "仓库ID") @RequestParam(required = false) Long warehouseId) {
        LambdaQueryWrapper<Stock> w = new LambdaQueryWrapper<>();
        w.eq(warehouseId != null, Stock::getWarehouseId, warehouseId);
        w.gt(Stock::getQty, 0);  // 排除0库存
        w.orderByDesc(Stock::getUpdateTime);

        IPage<Stock> page = stockService.page(new Page<>(current, size), w);
        fillStockNames(page.getRecords());
        return Result.ok(page);
    }

    private void fillStockNames(List<Stock> stocks) {
        if (stocks.isEmpty()) return;
        Map<Long, String> gm = goodsService.listByIds(
                stocks.stream().map(Stock::getGoodsId).distinct().collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Goods::getId, Goods::getGoodsName));
        Map<Long, String> km = kwService.listByIds(
                stocks.stream().map(Stock::getKwId).distinct().collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Kw::getId, Kw::getKwName));
        Map<Long, String> wm = warehouseService.listByIds(
                stocks.stream().map(Stock::getWarehouseId).distinct().collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Warehouse::getId, Warehouse::getWarehouseName));

        stocks.forEach(s -> {
            s.setGoodsName(gm.get(s.getGoodsId()));
            s.setKwName(km.get(s.getKwId()));
            s.setWarehouseName(wm.get(s.getWarehouseId()));
        });
    }
}
