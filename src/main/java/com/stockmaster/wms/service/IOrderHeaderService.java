package com.stockmaster.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stockmaster.wms.entity.OrderHeader;

public interface IOrderHeaderService extends IService<OrderHeader> {

    /**
     * 生成订单号：yyyyMMdd + 4位序号（当天递增）
     */
    String generateOrderNo();

    /**
     * 主子表联动保存（含订单号自动生成）
     */
    boolean saveWithLines(OrderHeader header);
}
