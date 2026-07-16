package com.stockmaster.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stockmaster.wms.entity.AuditIn;

public interface IAuditInService extends IService<AuditIn> {

    /**
     * 从入库单生成质检记录（状态：待验收 → 验收中）
     */
    void generateFromOrder(Long headerId);

    /**
     * 执行质检操作
     * @param auditId  质检记录ID
     * @param qualifiedQty   合格数量
     * @param unqualifiedQty 不合格数量
     * @param remark   备注
     */
    void operate(Long auditId, java.math.BigDecimal qualifiedQty,
                 java.math.BigDecimal unqualifiedQty, String remark);
}
