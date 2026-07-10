package com.stockmaster.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stockmaster.wms.entity.Kw;
import com.stockmaster.wms.entity.Warehouse;
import com.stockmaster.wms.entity.WarehouseArea;
import com.stockmaster.wms.mapper.KwMapper;
import com.stockmaster.wms.service.IKwService;
import com.stockmaster.wms.service.IWarehouseAreaService;
import com.stockmaster.wms.service.IWarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KwServiceImpl extends ServiceImpl<KwMapper, Kw> implements IKwService {

    private final IWarehouseAreaService areaService;
    private final IWarehouseService warehouseService;

    @Override
    public IPage<Kw> pageWithRelation(IPage<Kw> page, Kw condition) {
        LambdaQueryWrapper<Kw> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(condition.getKwName()), Kw::getKwName, condition.getKwName());
        wrapper.eq(condition.getAreaId() != null, Kw::getAreaId, condition.getAreaId());
        wrapper.eq(condition.getWarehouseId() != null, Kw::getWarehouseId, condition.getWarehouseId());
        wrapper.eq(condition.getStatus() != null, Kw::getStatus, condition.getStatus());
        wrapper.orderByDesc(Kw::getCreateTime);

        IPage<Kw> result = this.page(page, wrapper);

        if (result.getRecords().isEmpty()) return result;

        // 批量查库区名
        Map<Long, String> areaMap = areaService.listByIds(
                result.getRecords().stream().map(Kw::getAreaId).filter(id -> id != null).distinct().collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(WarehouseArea::getId, WarehouseArea::getAreaName));

        // 批量查仓库名
        Map<Long, String> whMap = warehouseService.listByIds(
                result.getRecords().stream().map(Kw::getWarehouseId).filter(id -> id != null).distinct().collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(Warehouse::getId, Warehouse::getWarehouseName));

        result.getRecords().forEach(k -> {
            k.setAreaName(areaMap.get(k.getAreaId()));
            k.setWarehouseName(whMap.get(k.getWarehouseId()));
        });
        return result;
    }
}
