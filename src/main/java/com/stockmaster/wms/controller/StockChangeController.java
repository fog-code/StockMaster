package com.stockmaster.wms.controller;

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

@Tag(name = "库存调整", description = "库存数量调整 + 调整记录查询")
@RestController
@RequestMapping("/stock/change")
@RequiredArgsConstructor
public class StockChangeController {

    private final IStockChangeService changeService;
    private final IGoodsService goodsService;
    private final IKwService kwService;

    @PostMapping("/adjust")
    @Operation(summary = "调整库存数量")
    public Result<String> adjust(
            @Parameter(description = "库存记录ID") @RequestParam Long stockId,
            @Parameter(description = "新数量") @RequestParam BigDecimal newQty,
            @Parameter(description = "备注") @RequestParam(required = false) String remark) {
        try {
            changeService.adjustQty(stockId, newQty, remark);
            return Result.ok("调整成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询调整记录")
    public Result<IPage<StockChange>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size) {
        IPage<StockChange> page = changeService.page(new Page<>(current, size),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StockChange>()
                        .orderByDesc(StockChange::getCreateTime));
        fillNames(page.getRecords());
        return Result.ok(page);
    }

    private void fillNames(List<StockChange> changes) {
        if (changes.isEmpty()) return;
        Map<Long, String> gm = goodsService.listByIds(
                changes.stream().map(StockChange::getGoodsId).distinct().collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Goods::getId, Goods::getGoodsName));
        Map<Long, String> km = kwService.listByIds(
                changes.stream().map(StockChange::getKwId).distinct().collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Kw::getId, Kw::getKwName));
        changes.forEach(c -> { c.setGoodsName(gm.get(c.getGoodsId())); c.setKwName(km.get(c.getKwId())); });
    }
}
