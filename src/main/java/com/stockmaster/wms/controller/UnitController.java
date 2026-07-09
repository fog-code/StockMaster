package com.stockmaster.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stockmaster.common.Result;
import com.stockmaster.wms.entity.Unit;
import com.stockmaster.wms.service.IUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "单位管理", description = "计量单位的增删改查")
@RestController
@RequestMapping("/basic/unit")
@RequiredArgsConstructor
public class UnitController {

    private final IUnitService unitService;

    @Operation(summary = "分页查询")
    @GetMapping("/page")
    public Result<IPage<Unit>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "单位名称") @RequestParam(required = false) String name) {
        LambdaQueryWrapper<Unit> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Unit::getUnitName, name);
        wrapper.orderByDesc(Unit::getCreateTime);
        return Result.ok(unitService.page(new Page<>(current, size), wrapper));
    }

    @Operation(summary = "查询全部（下拉选择用）")
    @GetMapping("/list")
    public Result<List<Unit>> list() {
        return Result.ok(unitService.list());
    }

    @Operation(summary = "根据ID查询")
    @GetMapping("/{id}")
    public Result<Unit> getById(@PathVariable Long id) {
        Unit unit = unitService.getById(id);
        return unit != null ? Result.ok(unit) : Result.error("单位不存在");
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<Unit> add(@RequestBody Unit unit) {
        unitService.save(unit);
        return Result.ok("新增成功", unit);
    }

    @Operation(summary = "修改")
    @PutMapping
    public Result<Unit> update(@RequestBody Unit unit) {
        if (unit.getId() == null) return Result.error("ID不能为空");
        unitService.updateById(unit);
        return Result.ok("修改成功", unit);
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return unitService.removeById(id) ? Result.ok("删除成功", null) : Result.error("删除失败");
    }
}
