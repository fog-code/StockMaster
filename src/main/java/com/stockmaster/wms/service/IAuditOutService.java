package com.stockmaster.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stockmaster.wms.entity.AuditOut;

public interface IAuditOutService extends IService<AuditOut> {
    /** 从出库单生成下架记录（含FIFO推荐分配） */
    void generateFromOrder(Long headerId);
}
