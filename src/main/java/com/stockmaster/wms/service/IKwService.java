package com.stockmaster.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stockmaster.wms.entity.Kw;

public interface IKwService extends IService<Kw> {
    IPage<Kw> pageWithRelation(IPage<Kw> page, Kw condition);
}
