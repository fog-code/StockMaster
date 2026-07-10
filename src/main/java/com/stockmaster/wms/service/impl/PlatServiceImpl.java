package com.stockmaster.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stockmaster.wms.entity.Plat;
import com.stockmaster.wms.entity.Warehouse;
import com.stockmaster.wms.mapper.PlatMapper;
import com.stockmaster.wms.service.IPlatService;
import com.stockmaster.wms.service.IWarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlatServiceImpl extends ServiceImpl<PlatMapper, Plat> implements IPlatService {

    private final IWarehouseService warehouseService;

    @Override
    public IPage<Plat> pageWithWarehouse(IPage<Plat> page, Plat condition) {
        LambdaQueryWrapper<Plat> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(condition.getPlatName()), Plat::getPlatName, condition.getPlatName());
        wrapper.eq(condition.getWarehouseId() != null, Plat::getWarehouseId, condition.getWarehouseId());
        wrapper.eq(condition.getStatus() != null, Plat::getStatus, condition.getStatus());
        wrapper.orderByDesc(Plat::getCreateTime);

        IPage<Plat> result = this.page(page, wrapper);

        if (result.getRecords().isEmpty()) return result;

        Map<Long, String> whMap = warehouseService.listByIds(
                result.getRecords().stream().map(Plat::getWarehouseId)
                        .filter(id -> id != null).distinct().collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(Warehouse::getId, Warehouse::getWarehouseName));

        result.getRecords().forEach(p -> p.setWarehouseName(whMap.get(p.getWarehouseId())));
        return result;
    }
}
