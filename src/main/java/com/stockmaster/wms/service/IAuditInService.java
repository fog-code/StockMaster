package com.stockmaster.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stockmaster.wms.entity.AuditIn;

public interface IAuditInService extends IService<AuditIn> {

    /**
     * 从入库单生成质检记录（状态：待验收 → 验收中）
     */
    void generateFromOrder(Long headerId);
}
