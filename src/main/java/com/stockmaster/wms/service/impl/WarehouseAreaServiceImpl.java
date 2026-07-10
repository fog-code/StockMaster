package com.stockmaster.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stockmaster.wms.entity.Warehouse;
import com.stockmaster.wms.entity.WarehouseArea;
import com.stockmaster.wms.mapper.WarehouseAreaMapper;
import com.stockmaster.wms.service.IWarehouseAreaService;
import com.stockmaster.wms.service.IWarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseAreaServiceImpl extends ServiceImpl<WarehouseAreaMapper, WarehouseArea>
        implements IWarehouseAreaService {

    private final IWarehouseService warehouseService;

    @Override
    public IPage<WarehouseArea> pageWithWarehouse(IPage<WarehouseArea> page, WarehouseArea condition) {
        LambdaQueryWrapper<WarehouseArea> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(condition.getAreaName()), WarehouseArea::getAreaName, condition.getAreaName());
        wrapper.eq(condition.getWarehouseId() != null, WarehouseArea::getWarehouseId, condition.getWarehouseId());
        wrapper.orderByDesc(WarehouseArea::getCreateTime);

        IPage<WarehouseArea> result = this.page(page, wrapper);

        if (result.getRecords().isEmpty()) return result;

        // 批量查仓库名
        Map<Long, String> whMap = warehouseService.listByIds(
                result.getRecords().stream().map(WarehouseArea::getWarehouseId)
                        .filter(id -> id != null).distinct().collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(Warehouse::getId, Warehouse::getWarehouseName));

        result.getRecords().forEach(a -> a.setWarehouseName(whMap.get(a.getWarehouseId())));
        return result;
    }
}
