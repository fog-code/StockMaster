package com.stockmaster.wms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stockmaster.common.Result;
import com.stockmaster.wms.entity.Kw;
import com.stockmaster.wms.service.IKwService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "库位管理", description = "库位的增删改查（关联库区+仓库）")
@RestController
@RequestMapping("/basic/kw")
@RequiredArgsConstructor
public class KwController {

    private final IKwService kwService;

    @Operation(summary = "分页查询（含库区名、仓库名）")
    @GetMapping("/page")
    public Result<IPage<Kw>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "库位名称") @RequestParam(required = false) String name,
            @Parameter(description = "库区ID") @RequestParam(required = false) Long areaId,
            @Parameter(description = "仓库ID") @RequestParam(required = false) Long warehouseId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        Kw condition = new Kw();
        condition.setKwName(name);
        condition.setAreaId(areaId);
        condition.setWarehouseId(warehouseId);
        condition.setStatus(status);
        return Result.ok(kwService.pageWithRelation(new Page<>(current, size), condition));
    }

    @Operation(summary = "查询全部（下拉选择用）")
    @GetMapping("/list")
    public Result<List<Kw>> list(
            @Parameter(description = "仓库ID") @RequestParam(required = false) Long warehouseId) {
        Kw condition = new Kw();
        condition.setWarehouseId(warehouseId);
        return Result.ok(kwService.pageWithRelation(
                new Page<>(1, 9999), condition).getRecords());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询")
    public Result<Kw> getById(@PathVariable Long id) {
        Kw kw = kwService.getById(id);
        return kw != null ? Result.ok(kw) : Result.error("库位不存在");
    }

    @PostMapping
    @Operation(summary = "新增")
    public Result<Kw> add(@RequestBody Kw kw) {
        kwService.save(kw);
        return Result.ok("新增成功", kw);
    }

    @PutMapping
    @Operation(summary = "修改")
    public Result<Kw> update(@RequestBody Kw kw) {
        if (kw.getId() == null) return Result.error("ID不能为空");
        kwService.updateById(kw);
        return Result.ok("修改成功", kw);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    public Result<Void> delete(@PathVariable Long id) {
        return kwService.removeById(id) ? Result.ok("删除成功", null) : Result.error("删除失败");
    }
}
