package com.stockmaster.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stockmaster.common.Result;
import com.stockmaster.wms.entity.Warehouse;
import com.stockmaster.wms.service.IWarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 仓库管理 Controller
 * 
 * 三层架构说明：
 *   Controller → Service(接口) → ServiceImpl(实现) → Mapper → DB
 *   本层只负责：接收请求、参数校验、调用Service、返回结果
 */
@Tag(name = "仓库管理", description = "仓库的增删改查接口")
@RestController
@RequestMapping("/basic/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final IWarehouseService warehouseService;

    // ==================== 查询 ====================

    @Operation(summary = "分页查询仓库列表")
    @GetMapping("/page")
    public Result<IPage<Warehouse>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "仓库名称(模糊搜索)") @RequestParam(required = false) String name,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Warehouse::getWarehouseName, name);
        wrapper.eq(status != null, Warehouse::getStatus, status);
        wrapper.orderByDesc(Warehouse::getCreateTime);

        Page<Warehouse> page = new Page<>(current, size);
        return Result.ok(warehouseService.page(page, wrapper));
    }

    @Operation(summary = "查询全部仓库（下拉选择用）")
    @GetMapping("/list")
    public Result<List<Warehouse>> list(
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, Warehouse::getStatus, status);
        wrapper.eq(Warehouse::getStatus, 1);  // 默认只查启用的
        wrapper.orderByDesc(Warehouse::getCreateTime);

        return Result.ok(warehouseService.list(wrapper));
    }

    @Operation(summary = "根据ID查询仓库")
    @GetMapping("/{id}")
    public Result<Warehouse> getById(@Parameter(description = "仓库ID") @PathVariable Long id) {
        Warehouse warehouse = warehouseService.getById(id);
        if (warehouse == null) {
            return Result.error("仓库不存在");
        }
        return Result.ok(warehouse);
    }

    // ==================== 新增 ====================

    @Operation(summary = "新增仓库")
    @PostMapping
    public Result<Warehouse> add(@RequestBody Warehouse warehouse) {
        warehouseService.save(warehouse);
        return Result.ok("新增成功", warehouse);
    }

    // ==================== 修改 ====================

    @Operation(summary = "修改仓库")
    @PutMapping
    public Result<Warehouse> update(@RequestBody Warehouse warehouse) {
        if (warehouse.getId() == null) {
            return Result.error("ID不能为空");
        }
        warehouseService.updateById(warehouse);
        return Result.ok("修改成功", warehouse);
    }

    // ==================== 删除 ====================

    @Operation(summary = "删除仓库（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "仓库ID") @PathVariable Long id) {
        boolean removed = warehouseService.removeById(id);
        if (removed) {
            return Result.ok("删除成功", null);
        }
        return Result.error("删除失败，仓库不存在");
    }

    @Operation(summary = "批量删除仓库")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@Parameter(description = "ID列表") @RequestBody List<Long> ids) {
        warehouseService.removeByIds(ids);
        return Result.ok("批量删除成功", null);
    }
}
