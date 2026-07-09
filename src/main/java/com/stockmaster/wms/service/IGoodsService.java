package com.stockmaster.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stockmaster.wms.entity.Goods;

public interface IGoodsService extends IService<Goods> {

    /**
     * 分页查询（含关联的分类名称和单位名称）
     */
    IPage<Goods> pageWithRelation(IPage<Goods> page, Goods goods);
}
