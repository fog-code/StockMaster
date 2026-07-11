package com.stockmaster.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stockmaster.common.Result;
import com.stockmaster.wms.entity.Supply;
import com.stockmaster.wms.service.ISupplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "供应商管理")
@RestController
@RequestMapping("/basic/supply")
@RequiredArgsConstructor
public class SupplyController {

    private final ISupplyService supplyService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public Result<IPage<Supply>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "供应商名称") @RequestParam(required = false) String name,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Supply> w = new LambdaQueryWrapper<>();
        w.like(StringUtils.hasText(name), Supply::getSupplyName, name);
        w.eq(status != null, Supply::getStatus, status);
        w.orderByDesc(Supply::getCreateTime);
        return Result.ok(supplyService.page(new Page<>(current, size), w));
    }

    @GetMapping("/list")
    @Operation(summary = "全部（下拉用）")
    public Result<List<Supply>> list() { return Result.ok(supplyService.list()); }

    @GetMapping("/{id}") public Result<Supply> get(@PathVariable Long id) {
        Supply s = supplyService.getById(id); return s != null ? Result.ok(s) : Result.error("不存在");
    }
    @PostMapping public Result<Supply> add(@RequestBody Supply s) { supplyService.save(s); return Result.ok("新增成功", s); }
    @PutMapping public Result<Supply> update(@RequestBody Supply s) {
        if (s.getId() == null) return Result.error("ID不能为空"); supplyService.updateById(s); return Result.ok("修改成功", s);
    }
    @DeleteMapping("/{id}") public Result<Void> delete(@PathVariable Long id) {
        return supplyService.removeById(id) ? Result.ok("删除成功", null) : Result.error("删除失败");
    }
}
