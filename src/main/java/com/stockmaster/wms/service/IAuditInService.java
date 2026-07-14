package com.stockmaster.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stockmaster.wms.entity.AuditIn;

public interface IAuditInService extends IService<AuditIn> {

    /**
     * 从入库单生成质检记录
     * 前提：入库单状态为"待验收"
     * 效果：为每条订单行创建一条待质检记录，入库单状态改为"验收中"
     *
     * @param headerId 入库单头ID
     * @return 生成的质检记录数
     */
    int generateFromOrder(Long headerId);
}
