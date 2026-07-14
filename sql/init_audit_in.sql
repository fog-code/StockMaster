-- ============================================
-- 质检模块 - 入库质检表
-- ============================================
USE stockmaster;

DROP TABLE IF EXISTS wms_audit_in;
CREATE TABLE wms_audit_in (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    header_id     BIGINT       NOT NULL COMMENT '入库单头ID',
    line_id       BIGINT       NOT NULL COMMENT '入库单行ID',
    goods_id      BIGINT       NOT NULL COMMENT '物料ID',
    unit_id       BIGINT       COMMENT '单位ID',
    plan_qty      DECIMAL(18,2) COMMENT '计划数量（来自入库单行）',
    real_qty      DECIMAL(18,2) COMMENT '实际质检数量（质检时填写）',
    status        VARCHAR(20)  DEFAULT '待质检' COMMENT '状态：待质检/合格/不合格',
    remark        VARCHAR(500) COMMENT '备注',
    create_by     VARCHAR(50)  COMMENT '创建人',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by     VARCHAR(50)  COMMENT '更新人',
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag      TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_header_id (header_id),
    INDEX idx_line_id (line_id),
    INDEX idx_goods_id (goods_id),
    INDEX idx_status (status)
) ENGINE=InnoDB COMMENT='入库质检记录';
