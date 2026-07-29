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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "盘点管理", description = "盘点单 CRUD（明盘/暗盘）")
@RestController
@RequestMapping("/stock/take")
@RequiredArgsConstructor
public class StockTakeController {

    private final IStockTakeService takeService;
    private final IGoodsService goodsService;
    private final IKwService kwService;
    private final IWarehouseService warehouseService;

    // ==================== 创建盘点单 ====================

    @PostMapping("/create-light")
    @Operation(summary = "创建明盘（人工填实盘数量）")
    public Result<String> createLight(
            @Parameter(description = "仓库ID") @RequestParam(required = false) Long warehouseId,
            @Parameter(description = "备注") @RequestParam(required = false) String remark) {
        takeService.createLightTake(warehouseId, remark);
        return Result.ok("明盘点单已创建", null);
    }

    @PostMapping("/create-dark")
    @Operation(summary = "创建暗盘（自动获取账面库存）")
    public Result<String> createDark(
            @Parameter(description = "仓库ID") @RequestParam(required = false) Long warehouseId) {
        takeService.createDarkTake(warehouseId);
        return Result.ok("暗盘点单已创建（含账面数量）", null);
    }

    // ==================== 盘点操作流程 ====================

    @PostMapping("/{id}/start")
    @Operation(summary = "开始盘点：待盘点→盘点中")
    public Result<String> startTake(@PathVariable Long id) {
        takeService.startTake(id);
        return Result.ok("已开始盘点，状态：盘点中", null);
    }

    @PutMapping("/{id}/fill")
    @Operation(summary = "填写实盘数量（自动计算差异）")
    public Result<StockTake> fillActual(
            @PathVariable Long id,
            @Parameter(description = "实盘数量") @RequestParam BigDecimal actualQty) {
        takeService.fillActual(id, actualQty);
        StockTake updated = takeService.getById(id);
        return Result.ok("实盘数量已填写，差异=" + updated.getDiffQty(), updated);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "完成盘点：盘点中→已完成")
    public Result<String> completeTake(@PathVariable Long id) {
        takeService.completeTake(id);
        return Result.ok("盘点已完成", null);
    }

    // ==================== 查询 ====================

    @GetMapping("/page")
    @Operation(summary = "分页查询盘点单")
    public Result<IPage<StockTake>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "类型") @RequestParam(required = false) String takeType,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        LambdaQueryWrapper<StockTake> w = new LambdaQueryWrapper<>();
        w.eq(takeType != null, StockTake::getTakeType, takeType);
        w.eq(status != null, StockTake::getStatus, status);
        w.orderByDesc(StockTake::getCreateTime);
        IPage<StockTake> page = takeService.page(new Page<>(current, size), w);
        fillNames(page.getRecords());
        return Result.ok(page);
    }

    @GetMapping("/diff")
    @Operation(summary = "差异明细分页（已完成且有差异的记录）")
    public Result<IPage<StockTake>> diff(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "类型") @RequestParam(required = false) String takeType) {
        IPage<StockTake> page = takeService.pageDiff(current, size, takeType);
        fillNames(page.getRecords());
        return Result.ok(page);
    }

    @PutMapping
    @Operation(summary = "修改盘点单")
    public Result<StockTake> update(@RequestBody StockTake take) {
        if (take.getId() == null) return Result.error("ID不能为空");
        takeService.updateById(take);
        return Result.ok("修改成功", take);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    public Result<Void> delete(@PathVariable Long id) {
        return takeService.removeById(id) ? Result.ok("删除成功", null) : Result.error("删除失败");
    }

    // ==================== 辅助 ====================

    private void fillNames(List<StockTake> takes) {
        if (takes.isEmpty()) return;
        Map<Long, String> gm = goodsService.listByIds(
                takes.stream().map(StockTake::getGoodsId).distinct().collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Goods::getId, Goods::getGoodsName));
        Map<Long, String> km = kwService.listByIds(
                takes.stream().map(StockTake::getKwId).distinct().collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Kw::getId, Kw::getKwName));
        Map<Long, String> wm = warehouseService.listByIds(
                takes.stream().map(StockTake::getWarehouseId).distinct().collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Warehouse::getId, Warehouse::getWarehouseName));
        takes.forEach(t -> {
            t.setGoodsName(gm.get(t.getGoodsId()));
            t.setKwName(km.get(t.getKwId()));
            t.setWarehouseName(wm.get(t.getWarehouseId()));
        });
    }
}
