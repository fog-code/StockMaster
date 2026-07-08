-- ============================================
-- StockMaster WMS 数据库初始化脚本
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS stockmaster
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE stockmaster;

-- ============================================
-- 1. 物料分类
-- ============================================
DROP TABLE IF EXISTS `wms_goods_type`;
CREATE TABLE `wms_goods_type` (
    `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type_code`   VARCHAR(32)  NOT NULL COMMENT '分类编码',
    `type_name`   VARCHAR(64)  NOT NULL COMMENT '分类名称',
    `parent_id`   BIGINT(20)   DEFAULT 0 COMMENT '父级ID',
    `sort_no`     INT(11)      DEFAULT 0 COMMENT '排序号',
    `del_flag`    TINYINT(1)   DEFAULT 0 COMMENT '删除标记(0-正常 1-删除)',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_code` (`type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料分类';

-- ============================================
-- 2. 单位
-- ============================================
DROP TABLE IF EXISTS `wms_unit`;
CREATE TABLE `wms_unit` (
    `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `unit_code`   VARCHAR(32)  NOT NULL COMMENT '单位编码',
    `unit_name`   VARCHAR(64)  NOT NULL COMMENT '单位名称',
    `del_flag`    TINYINT(1)   DEFAULT 0 COMMENT '删除标记',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_unit_code` (`unit_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='单位';

-- ============================================
-- 3. 物料
-- ============================================
DROP TABLE IF EXISTS `wms_goods`;
CREATE TABLE `wms_goods` (
    `id`           BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `goods_code`   VARCHAR(32)  NOT NULL COMMENT '物料编码',
    `goods_name`   VARCHAR(128) NOT NULL COMMENT '物料名称',
    `goods_type_id` BIGINT(20)  DEFAULT NULL COMMENT '物料分类ID',
    `unit_id`      BIGINT(20)   DEFAULT NULL COMMENT '单位ID',
    `spec`         VARCHAR(64)  DEFAULT NULL COMMENT '规格',
    `model`        VARCHAR(64)  DEFAULT NULL COMMENT '型号',
    `barcode`      VARCHAR(64)  DEFAULT NULL COMMENT '条码',
    `remark`       VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `del_flag`     TINYINT(1)   DEFAULT 0 COMMENT '删除标记',
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_goods_code` (`goods_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料';

-- ============================================
-- 4. 仓库
-- ============================================
DROP TABLE IF EXISTS `wms_warehouse`;
CREATE TABLE `wms_warehouse` (
    `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `wh_code`     VARCHAR(32)  NOT NULL COMMENT '仓库编码',
    `wh_name`     VARCHAR(64)  NOT NULL COMMENT '仓库名称',
    `wh_type`     VARCHAR(16)  DEFAULT NULL COMMENT '仓库类型(常温/冷藏/危化品)',
    `address`     VARCHAR(255) DEFAULT NULL COMMENT '仓库地址',
    `area`        DECIMAL(12,2) DEFAULT 0.00 COMMENT '面积(㎡)',
    `manager`     VARCHAR(32)  DEFAULT NULL COMMENT '负责人',
    `phone`       VARCHAR(16)  DEFAULT NULL COMMENT '联系电话',
    `status`      TINYINT(1)   DEFAULT 1 COMMENT '状态(0-禁用 1-启用)',
    `del_flag`    TINYINT(1)   DEFAULT 0 COMMENT '删除标记',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_wh_code` (`wh_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库';

-- ============================================
-- 5. 库区
-- ============================================
DROP TABLE IF EXISTS `wms_warehouse_area`;
CREATE TABLE `wms_warehouse_area` (
    `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `area_code`     VARCHAR(32)  NOT NULL COMMENT '库区编码',
    `area_name`     VARCHAR(64)  NOT NULL COMMENT '库区名称',
    `warehouse_id`  BIGINT(20)   NOT NULL COMMENT '所属仓库ID',
    `area_type`     VARCHAR(16)  DEFAULT NULL COMMENT '库区类型(收货区/存储区/发货区/退货区)',
    `max_capacity`  INT(11)      DEFAULT 0 COMMENT '最大容量(托)',
    `del_flag`      TINYINT(1)   DEFAULT 0 COMMENT '删除标记',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_area_code` (`area_code`),
    KEY `idx_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库区';

-- ============================================
-- 6. 库位
-- ============================================
DROP TABLE IF EXISTS `wms_kw`;
CREATE TABLE `wms_kw` (
    `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `kw_code`       VARCHAR(32)  NOT NULL COMMENT '库位编码',
    `kw_name`       VARCHAR(64)  NOT NULL COMMENT '库位名称',
    `area_id`       BIGINT(20)   NOT NULL COMMENT '所属库区ID',
    `warehouse_id`  BIGINT(20)   NOT NULL COMMENT '所属仓库ID',
    `max_volume`    DECIMAL(12,2) DEFAULT 0.00 COMMENT '最大容积(m³)',
    `max_weight`    DECIMAL(12,2) DEFAULT 0.00 COMMENT '最大承重(kg)',
    `kw_type`       VARCHAR(16)  DEFAULT 'GROUND' COMMENT '类型(GROUND-地面/SHELF-货架)',
    `del_flag`      TINYINT(1)   DEFAULT 0 COMMENT '删除标记',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_kw_code` (`kw_code`),
    KEY `idx_area_id` (`area_id`),
    KEY `idx_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库位';

-- ============================================
-- 7. 月台
-- ============================================
DROP TABLE IF EXISTS `wms_plat`;
CREATE TABLE `wms_plat` (
    `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `plat_code`     VARCHAR(32)  NOT NULL COMMENT '月台编码',
    `plat_name`     VARCHAR(64)  NOT NULL COMMENT '月台名称',
    `warehouse_id`  BIGINT(20)   NOT NULL COMMENT '所属仓库ID',
    `plat_type`     VARCHAR(16)  DEFAULT 'IN' COMMENT '类型(IN-收货/OUT-发货/ALL-通用)',
    `del_flag`      TINYINT(1)   DEFAULT 0 COMMENT '删除标记',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_plat_code` (`plat_code`),
    KEY `idx_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='月台';

-- ============================================
-- 8. 供应商
-- ============================================
DROP TABLE IF EXISTS `wms_supply`;
CREATE TABLE `wms_supply` (
    `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `supply_code` VARCHAR(32)  NOT NULL COMMENT '供应商编码',
    `supply_name` VARCHAR(128) NOT NULL COMMENT '供应商名称',
    `contacts`    VARCHAR(32)  DEFAULT NULL COMMENT '联系人',
    `phone`       VARCHAR(16)  DEFAULT NULL COMMENT '联系电话',
    `address`     VARCHAR(255) DEFAULT NULL COMMENT '地址',
    `remark`      VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `del_flag`    TINYINT(1)   DEFAULT 0 COMMENT '删除标记',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_supply_code` (`supply_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商';

-- ============================================
-- 9. 客户
-- ============================================
DROP TABLE IF EXISTS `wms_cus`;
CREATE TABLE `wms_cus` (
    `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `cus_code`    VARCHAR(32)  NOT NULL COMMENT '客户编码',
    `cus_name`    VARCHAR(128) NOT NULL COMMENT '客户名称',
    `contacts`    VARCHAR(32)  DEFAULT NULL COMMENT '联系人',
    `phone`       VARCHAR(16)  DEFAULT NULL COMMENT '联系电话',
    `address`     VARCHAR(255) DEFAULT NULL COMMENT '地址',
    `remark`      VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `del_flag`    TINYINT(1)   DEFAULT 0 COMMENT '删除标记',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_cus_code` (`cus_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户';

-- ============================================
-- 10. 订单类型
-- ============================================
DROP TABLE IF EXISTS `wms_order_type`;
CREATE TABLE `wms_order_type` (
    `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type_code`   VARCHAR(32)  NOT NULL COMMENT '类型编码',
    `type_name`   VARCHAR(64)  NOT NULL COMMENT '类型名称',
    `direction`   VARCHAR(8)   NOT NULL COMMENT '方向(IN-入库/OUT-出库)',
    `del_flag`    TINYINT(1)   DEFAULT 0 COMMENT '删除标记',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_code` (`type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单类型';

-- ============================================
-- 11. 部件类型
-- ============================================
DROP TABLE IF EXISTS `wms_comtype`;
CREATE TABLE `wms_comtype` (
    `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type_code`   VARCHAR(32)  NOT NULL COMMENT '类型编码',
    `type_name`   VARCHAR(64)  NOT NULL COMMENT '类型名称',
    `del_flag`    TINYINT(1)   DEFAULT 0 COMMENT '删除标记',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_code` (`type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部件类型';

-- ============================================
-- 初始化基础数据
-- ============================================

-- 单位
INSERT INTO `wms_unit` (`unit_code`, `unit_name`) VALUES
('PCS', '件'), ('BOX', '箱'), ('KG', '千克'), ('M', '米'),
('L', '升'), ('PAL', '托'), ('BAG', '袋'), ('ROL', '卷');

-- 物料分类
INSERT INTO `wms_goods_type` (`type_code`, `type_name`, `sort_no`) VALUES
('RAW', '原材料', 1),
('FIN', '成品', 2),
('PKG', '包材', 3),
('SEMI', '半成品', 4);

-- 订单类型
INSERT INTO `wms_order_type` (`type_code`, `type_name`, `direction`) VALUES
('PURCHASE_IN', '采购入库', 'IN'),
('RETURN_IN', '退货入库', 'IN'),
('TRANSFER_IN', '调拨入库', 'IN'),
('SALE_OUT', '销售出库', 'OUT'),
('PICKING_OUT', '领料出库', 'OUT'),
('TRANSFER_OUT', '调拨出库', 'OUT');

-- 部件类型
INSERT INTO `wms_comtype` (`type_code`, `type_name`) VALUES
('NORMAL', '普通件'),('FRAGILE', '易碎件'),('HAZARD', '危化品'),
('COLD', '冷藏件'),('BULK', '大件');

-- 仓库
INSERT INTO `wms_warehouse` (`wh_code`, `wh_name`, `wh_type`, `address`, `manager`, `phone`) VALUES
('WH-001', '一号仓库', '常温', '工业园区A区1号', '张三', '13800000001'),
('WH-002', '二号仓库', '冷藏', '工业园区A区2号', '李四', '13800000002');

-- 库区
INSERT INTO `wms_warehouse_area` (`area_code`, `area_name`, `warehouse_id`, `area_type`, `max_capacity`) VALUES
('AREA-01', '收货区', 1, '收货区', 200),
('AREA-02', '存储区A', 1, '存储区', 500),
('AREA-03', '发货区', 1, '发货区', 200),
('AREA-04', '存储区B', 2, '存储区', 300);

-- 库位 (每个存储区 8 个库位)
INSERT INTO `wms_kw` (`kw_code`, `kw_name`, `area_id`, `warehouse_id`, `kw_type`) VALUES
('KW-A01', 'A货架01', 2, 1, 'SHELF'), ('KW-A02', 'A货架02', 2, 1, 'SHELF'),
('KW-A03', 'A货架03', 2, 1, 'SHELF'), ('KW-A04', 'A货架04', 2, 1, 'SHELF'),
('KW-A05', 'A货架05', 2, 1, 'SHELF'), ('KW-A06', 'A货架06', 2, 1, 'SHELF'),
('KW-A07', 'A货架07', 2, 1, 'SHELF'), ('KW-A08', 'A货架08', 2, 1, 'SHELF'),
('KW-B01', 'B货架01', 4, 2, 'SHELF'), ('KW-B02', 'B货架02', 4, 2, 'SHELF'),
('KW-B03', 'B货架03', 4, 2, 'SHELF'), ('KW-B04', 'B货架04', 4, 2, 'SHELF'),
('KW-B05', 'B货架05', 4, 2, 'SHELF'), ('KW-B06', 'B货架06', 4, 2, 'SHELF'),
('KW-B07', 'B货架07', 4, 2, 'SHELF'), ('KW-B08', 'B货架08', 4, 2, 'SHELF');

-- 月台
INSERT INTO `wms_plat` (`plat_code`, `plat_name`, `warehouse_id`, `plat_type`) VALUES
('PLAT-01', '1号月台', 1, 'ALL'),
('PLAT-02', '2号月台', 1, 'IN'),
('PLAT-03', '3号月台', 1, 'OUT');
