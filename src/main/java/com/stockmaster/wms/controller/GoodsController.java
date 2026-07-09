package com.stockmaster.wms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stockmaster.common.Result;
import com.stockmaster.wms.entity.Goods;
import com.stockmaster.wms.service.IGoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "物料管理", description = "物料的增删改查（关联分类和单位）")
@RestController
@RequestMapping("/basic/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final IGoodsService goodsService;

    @Operation(summary = "分页查询（含分类名、单位名）")
    @GetMapping("/page")
    public Result<IPage<Goods>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "物料名称") @RequestParam(required = false) String name,
            @Parameter(description = "物料编码") @RequestParam(required = false) String code,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long typeId) {
        Goods condition = new Goods();
        condition.setGoodsName(name);
        condition.setGoodsCode(code);
        condition.setGoodsTypeId(typeId);
        return Result.ok(goodsService.pageWithRelation(new Page<>(current, size), condition));
    }

    @Operation(summary = "查询全部（下拉选择用）")
    @GetMapping("/list")
    public Result<List<Goods>> list(
            @Parameter(description = "分类ID") @RequestParam(required = false) Long typeId) {
        Goods condition = new Goods();
        condition.setGoodsTypeId(typeId);
        return Result.ok(goodsService.pageWithRelation(
                new Page<>(1, 9999), condition).getRecords());
    }

    @Operation(summary = "根据ID查询（含分类名、单位名）")
    @GetMapping("/{id}")
    public Result<Goods> getById(@PathVariable Long id) {
        Goods condition = new Goods();
        IPage<Goods> result = goodsService.pageWithRelation(new Page<>(1, 1), condition);
        return result.getRecords().isEmpty()
                ? Result.error("物料不存在")
                : Result.ok(result.getRecords().get(0));
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<Goods> add(@RequestBody Goods goods) {
        goodsService.save(goods);
        return Result.ok("新增成功", goods);
    }

    @Operation(summary = "修改")
    @PutMapping
    public Result<Goods> update(@RequestBody Goods goods) {
        if (goods.getId() == null) return Result.error("ID不能为空");
        goodsService.updateById(goods);
        return Result.ok("修改成功", goods);
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return goodsService.removeById(id)
                ? Result.ok("删除成功", null) : Result.error("删除失败");
    }
}
