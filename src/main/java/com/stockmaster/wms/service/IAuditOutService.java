package com.stockmaster.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stockmaster.wms.entity.AuditOut;

public interface IAuditOutService extends IService<AuditOut> {
    void generateFromOrder(Long headerId);
    void pickdown(Long auditOutId);
}
