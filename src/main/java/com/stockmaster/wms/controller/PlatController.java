package com.stockmaster.wms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stockmaster.common.Result;
import com.stockmaster.wms.entity.Plat;
import com.stockmaster.wms.service.IPlatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "月台管理", description = "月台的增删改查（关联仓库）")
@RestController
@RequestMapping("/basic/plat")
@RequiredArgsConstructor
public class PlatController {

    private final IPlatService platService;

    @Operation(summary = "分页查询（含仓库名）")
    @GetMapping("/page")
    public Result<IPage<Plat>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "月台名称") @RequestParam(required = false) String name,
            @Parameter(description = "仓库ID") @RequestParam(required = false) Long warehouseId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        Plat condition = new Plat();
        condition.setPlatName(name);
        condition.setWarehouseId(warehouseId);
        condition.setStatus(status);
        return Result.ok(platService.pageWithWarehouse(new Page<>(current, size), condition));
    }

    @Operation(summary = "查询全部（下拉选择用）")
    @GetMapping("/list")
    public Result<List<Plat>> list(
            @Parameter(description = "仓库ID") @RequestParam(required = false) Long warehouseId) {
        Plat condition = new Plat();
        condition.setWarehouseId(warehouseId);
        return Result.ok(platService.pageWithWarehouse(
                new Page<>(1, 9999), condition).getRecords());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询")
    public Result<Plat> getById(@PathVariable Long id) {
        Plat plat = platService.getById(id);
        return plat != null ? Result.ok(plat) : Result.error("月台不存在");
    }

    @PostMapping
    @Operation(summary = "新增")
    public Result<Plat> add(@RequestBody Plat plat) {
        platService.save(plat);
        return Result.ok("新增成功", plat);
    }

    @PutMapping
    @Operation(summary = "修改")
    public Result<Plat> update(@RequestBody Plat plat) {
        if (plat.getId() == null) return Result.error("ID不能为空");
        platService.updateById(plat);
        return Result.ok("修改成功", plat);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    public Result<Void> delete(@PathVariable Long id) {
        return platService.removeById(id) ? Result.ok("删除成功", null) : Result.error("删除失败");
    }
}
