package com.stockmaster.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stockmaster.common.Result;
import com.stockmaster.wms.entity.Comtype;
import com.stockmaster.wms.service.IComtypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "部件类型管理")
@RestController
@RequestMapping("/basic/comtype")
@RequiredArgsConstructor
public class ComtypeController {

    private final IComtypeService comtypeService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public Result<IPage<Comtype>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "类型名称") @RequestParam(required = false) String name) {
        LambdaQueryWrapper<Comtype> w = new LambdaQueryWrapper<>();
        w.like(StringUtils.hasText(name), Comtype::getTypeName, name);
        w.orderByAsc(Comtype::getParentId).orderByDesc(Comtype::getCreateTime);
        return Result.ok(comtypeService.page(new Page<>(current, size), w));
    }

    @GetMapping("/children")
    @Operation(summary = "查询子类型")
    public Result<List<Comtype>> children(@Parameter(description = "父级ID") @RequestParam(defaultValue = "0") Long parentId) {
        return Result.ok(comtypeService.list(new LambdaQueryWrapper<Comtype>().eq(Comtype::getParentId, parentId)));
    }

    @GetMapping("/list") @Operation(summary = "全部（下拉用）")
    public Result<List<Comtype>> list() { return Result.ok(comtypeService.list()); }

    @GetMapping("/{id}") public Result<Comtype> get(@PathVariable Long id) {
        Comtype c = comtypeService.getById(id); return c != null ? Result.ok(c) : Result.error("不存在");
    }
    @PostMapping public Result<Comtype> add(@RequestBody Comtype c) { comtypeService.save(c); return Result.ok("新增成功", c); }
    @PutMapping public Result<Comtype> update(@RequestBody Comtype c) {
        if (c.getId() == null) return Result.error("ID不能为空"); comtypeService.updateById(c); return Result.ok("修改成功", c);
    }
    @DeleteMapping("/{id}") public Result<Void> delete(@PathVariable Long id) {
        comtypeService.removeById(id);
        comtypeService.remove(new LambdaQueryWrapper<Comtype>().eq(Comtype::getParentId, id));  // 级联删除子类型
        return Result.ok("删除成功", null);
    }
}
