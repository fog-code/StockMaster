package com.stockmaster.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stockmaster.common.Result;
import com.stockmaster.wms.entity.GoodsType;
import com.stockmaster.wms.service.IGoodsTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "物料分类", description = "物料分类的增删改查")
@RestController
@RequestMapping("/basic/goods-type")
@RequiredArgsConstructor
public class GoodsTypeController {

    private final IGoodsTypeService goodsTypeService;

    @Operation(summary = "分页查询")
    @GetMapping("/page")
    public Result<IPage<GoodsType>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "分类名称") @RequestParam(required = false) String name) {
        LambdaQueryWrapper<GoodsType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), GoodsType::getTypeName, name);
        wrapper.orderByAsc(GoodsType::getParentId).orderByDesc(GoodsType::getCreateTime);
        return Result.ok(goodsTypeService.page(new Page<>(current, size), wrapper));
    }

    @Operation(summary = "查询全部（下拉选择用）")
    @GetMapping("/list")
    public Result<List<GoodsType>> list() {
        return Result.ok(goodsTypeService.list());
    }

    @Operation(summary = "查询子分类（按父级ID）")
    @GetMapping("/children")
    public Result<List<GoodsType>> children(
            @Parameter(description = "父级ID，0=顶级") @RequestParam(defaultValue = "0") Long parentId) {
        LambdaQueryWrapper<GoodsType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsType::getParentId, parentId);
        return Result.ok(goodsTypeService.list(wrapper));
    }

    @Operation(summary = "根据ID查询")
    @GetMapping("/{id}")
    public Result<GoodsType> getById(@PathVariable Long id) {
        GoodsType type = goodsTypeService.getById(id);
        return type != null ? Result.ok(type) : Result.error("分类不存在");
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<GoodsType> add(@RequestBody GoodsType goodsType) {
        goodsTypeService.save(goodsType);
        return Result.ok("新增成功", goodsType);
    }

    @Operation(summary = "修改")
    @PutMapping
    public Result<GoodsType> update(@RequestBody GoodsType goodsType) {
        if (goodsType.getId() == null) return Result.error("ID不能为空");
        goodsTypeService.updateById(goodsType);
        return Result.ok("修改成功", goodsType);
    }

    @Operation(summary = "删除（含子分类一并逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        // 删除当前分类及子分类
        goodsTypeService.removeById(id);
        goodsTypeService.remove(new LambdaQueryWrapper<GoodsType>().eq(GoodsType::getParentId, id));
        return Result.ok("删除成功", null);
    }
}
