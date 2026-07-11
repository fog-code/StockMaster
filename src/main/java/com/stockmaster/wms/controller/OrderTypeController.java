package com.stockmaster.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stockmaster.common.Result;
import com.stockmaster.wms.entity.OrderType;
import com.stockmaster.wms.service.IOrderTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "订单类型管理")
@RestController
@RequestMapping("/basic/order-type")
@RequiredArgsConstructor
public class OrderTypeController {

    private final IOrderTypeService orderTypeService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public Result<IPage<OrderType>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "类型名称") @RequestParam(required = false) String name,
            @Parameter(description = "方向: IN/OUT") @RequestParam(required = false) String direction) {
        LambdaQueryWrapper<OrderType> w = new LambdaQueryWrapper<>();
        w.like(StringUtils.hasText(name), OrderType::getTypeName, name);
        w.eq(StringUtils.hasText(direction), OrderType::getDirection, direction);
        w.orderByDesc(OrderType::getCreateTime);
        return Result.ok(orderTypeService.page(new Page<>(current, size), w));
    }

    @GetMapping("/list") @Operation(summary = "全部（下拉用）")
    public Result<List<OrderType>> list() { return Result.ok(orderTypeService.list()); }

    @GetMapping("/{id}") public Result<OrderType> get(@PathVariable Long id) {
        OrderType o = orderTypeService.getById(id); return o != null ? Result.ok(o) : Result.error("不存在");
    }
    @PostMapping public Result<OrderType> add(@RequestBody OrderType o) { orderTypeService.save(o); return Result.ok("新增成功", o); }
    @PutMapping public Result<OrderType> update(@RequestBody OrderType o) {
        if (o.getId() == null) return Result.error("ID不能为空"); orderTypeService.updateById(o); return Result.ok("修改成功", o);
    }
    @DeleteMapping("/{id}") public Result<Void> delete(@PathVariable Long id) {
        return orderTypeService.removeById(id) ? Result.ok("删除成功", null) : Result.error("删除失败");
    }
}
