package com.stockmaster.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stockmaster.wms.entity.Goods;
import com.stockmaster.wms.entity.GoodsType;
import com.stockmaster.wms.entity.Unit;
import com.stockmaster.wms.mapper.GoodsMapper;
import com.stockmaster.wms.service.IGoodsService;
import com.stockmaster.wms.service.IGoodsTypeService;
import com.stockmaster.wms.service.IUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    private final IGoodsTypeService goodsTypeService;
    private final IUnitService unitService;

    @Override
    public IPage<Goods> pageWithRelation(IPage<Goods> page, Goods condition) {
        // 1. 构造查询条件
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(condition.getGoodsName()), Goods::getGoodsName, condition.getGoodsName());
        wrapper.like(StringUtils.hasText(condition.getGoodsCode()), Goods::getGoodsCode, condition.getGoodsCode());
        wrapper.eq(condition.getGoodsTypeId() != null, Goods::getGoodsTypeId, condition.getGoodsTypeId());
        wrapper.orderByDesc(Goods::getCreateTime);

        // 2. 分页查询
        IPage<Goods> result = this.page(page, wrapper);

        if (result.getRecords().isEmpty()) {
            return result;
        }

        // 3. 批量查关联的分类名称和单位名称，组装到结果里
        List<Long> typeIds = result.getRecords().stream()
                .map(Goods::getGoodsTypeId).filter(id -> id != null).distinct().collect(Collectors.toList());
        List<Long> unitIds = result.getRecords().stream()
                .map(Goods::getUnitId).filter(id -> id != null).distinct().collect(Collectors.toList());

        Map<Long, String> typeNameMap = typeIds.isEmpty() ? Map.of() :
                goodsTypeService.listByIds(typeIds).stream()
                        .collect(Collectors.toMap(GoodsType::getId, GoodsType::getTypeName));
        Map<Long, String> unitNameMap = unitIds.isEmpty() ? Map.of() :
                unitService.listByIds(unitIds).stream()
                        .collect(Collectors.toMap(Unit::getId, Unit::getUnitName));

        result.getRecords().forEach(g -> {
            g.setGoodsTypeName(typeNameMap.get(g.getGoodsTypeId()));
            g.setUnitName(unitNameMap.get(g.getUnitId()));
        });

        return result;
    }
}
