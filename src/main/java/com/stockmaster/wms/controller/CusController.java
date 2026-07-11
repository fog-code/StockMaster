package com.stockmaster.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stockmaster.common.Result;
import com.stockmaster.wms.entity.Cus;
import com.stockmaster.wms.service.ICusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "客户管理")
@RestController
@RequestMapping("/basic/cus")
@RequiredArgsConstructor
public class CusController {

    private final ICusService cusService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public Result<IPage<Cus>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "客户名称") @RequestParam(required = false) String name,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Cus> w = new LambdaQueryWrapper<>();
        w.like(StringUtils.hasText(name), Cus::getCusName, name);
        w.eq(status != null, Cus::getStatus, status);
        w.orderByDesc(Cus::getCreateTime);
        return Result.ok(cusService.page(new Page<>(current, size), w));
    }

    @GetMapping("/list") @Operation(summary = "全部（下拉用）")
    public Result<List<Cus>> list() { return Result.ok(cusService.list()); }

    @GetMapping("/{id}") public Result<Cus> get(@PathVariable Long id) {
        Cus c = cusService.getById(id); return c != null ? Result.ok(c) : Result.error("不存在");
    }
    @PostMapping public Result<Cus> add(@RequestBody Cus c) { cusService.save(c); return Result.ok("新增成功", c); }
    @PutMapping public Result<Cus> update(@RequestBody Cus c) {
        if (c.getId() == null) return Result.error("ID不能为空"); cusService.updateById(c); return Result.ok("修改成功", c);
    }
    @DeleteMapping("/{id}") public Result<Void> delete(@PathVariable Long id) {
        return cusService.removeById(id) ? Result.ok("删除成功", null) : Result.error("删除失败");
    }
}
