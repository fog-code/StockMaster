package com.stockmaster.wms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stockmaster.common.Result;
import com.stockmaster.wms.entity.WarehouseArea;
import com.stockmaster.wms.service.IWarehouseAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "库区管理", description = "库区的增删改查（关联仓库）")
@RestController
@RequestMapping("/basic/warehouse-area")
@RequiredArgsConstructor
public class WarehouseAreaController {

    private final IWarehouseAreaService areaService;

    @Operation(summary = "分页查询（含仓库名）")
    @GetMapping("/page")
    public Result<IPage<WarehouseArea>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "库区名称") @RequestParam(required = false) String name,
            @Parameter(description = "仓库ID") @RequestParam(required = false) Long warehouseId) {
        WarehouseArea condition = new WarehouseArea();
        condition.setAreaName(name);
        condition.setWarehouseId(warehouseId);
        return Result.ok(areaService.pageWithWarehouse(new Page<>(current, size), condition));
    }

    @Operation(summary = "查询全部（下拉选择用）")
    @GetMapping("/list")
    public Result<List<WarehouseArea>> list(
            @Parameter(description = "仓库ID") @RequestParam(required = false) Long warehouseId) {
        WarehouseArea condition = new WarehouseArea();
        condition.setWarehouseId(warehouseId);
        return Result.ok(areaService.pageWithWarehouse(
                new Page<>(1, 9999), condition).getRecords());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询")
    public Result<WarehouseArea> getById(@PathVariable Long id) {
        return areaService.getById(id) != null
                ? Result.ok(areaService.getById(id)) : Result.error("库区不存在");
    }

    @PostMapping
    @Operation(summary = "新增")
    public Result<WarehouseArea> add(@RequestBody WarehouseArea area) {
        areaService.save(area);
        return Result.ok("新增成功", area);
    }

    @PutMapping
    @Operation(summary = "修改")
    public Result<WarehouseArea> update(@RequestBody WarehouseArea area) {
        if (area.getId() == null) return Result.error("ID不能为空");
        areaService.updateById(area);
        return Result.ok("修改成功", area);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    public Result<Void> delete(@PathVariable Long id) {
        return areaService.removeById(id) ? Result.ok("删除成功", null) : Result.error("删除失败");
    }
}
