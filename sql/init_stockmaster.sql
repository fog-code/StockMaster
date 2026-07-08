-- ============================================
-- StockMaster 仓库管理系统 - 初始化数据库
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS stockmaster
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE stockmaster;

-- ============================================
-- 1. 基础数据 - 物料分类
-- ============================================
DROP TABLE IF EXISTS wms_goods_type;
CREATE TABLE wms_goods_type (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    type_name   VARCHAR(100) NOT NULL COMMENT '分类名称',
    type_code   VARCHAR(50)  COMMENT '分类编码',
    parent_id   BIGINT       DEFAULT 0 COMMENT '父级ID，0=顶级',
    remark      VARCHAR(500) COMMENT '备注',
    create_by   VARCHAR(50)  COMMENT '创建人',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(50)  COMMENT '更新人',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag    TINYINT      DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除'
) ENGINE=InnoDB COMMENT='物料分类';

-- ============================================
-- 2. 基础数据 - 单位
-- ============================================
DROP TABLE IF EXISTS wms_unit;
CREATE TABLE wms_unit (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    unit_name   VARCHAR(50)  NOT NULL COMMENT '单位名称',
    unit_code   VARCHAR(20)  COMMENT '单位编码（个、箱、托等）',
    remark      VARCHAR(500) COMMENT '备注',
    create_by   VARCHAR(50)  COMMENT '创建人',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(50)  COMMENT '更新人',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag    TINYINT      DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB COMMENT='单位';

-- ============================================
-- 3. 基础数据 - 物料
-- ============================================
DROP TABLE IF EXISTS wms_goods;
CREATE TABLE wms_goods (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    goods_name    VARCHAR(200) NOT NULL COMMENT '物料名称',
    goods_code    VARCHAR(100) NOT NULL COMMENT '物料编码',
    goods_type_id BIGINT       COMMENT '物料分类ID',
    unit_id       BIGINT       COMMENT '单位ID',
    spec          VARCHAR(200) COMMENT '规格',
    model         VARCHAR(200) COMMENT '型号',
    barcode       VARCHAR(100) COMMENT '条码',
    remark        VARCHAR(500) COMMENT '备注',
    create_by     VARCHAR(50)  COMMENT '创建人',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by     VARCHAR(50)  COMMENT '更新人',
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag      TINYINT      DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB COMMENT='物料';

-- ============================================
-- 4. 基础数据 - 仓库
-- ============================================
DROP TABLE IF EXISTS wms_warehouse;
CREATE TABLE wms_warehouse (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    warehouse_name  VARCHAR(200) NOT NULL COMMENT '仓库名称',
    warehouse_code  VARCHAR(50)  NOT NULL COMMENT '仓库编码',
    address         VARCHAR(500) COMMENT '仓库地址',
    contact_person  VARCHAR(50)  COMMENT '联系人',
    contact_phone   VARCHAR(20)  COMMENT '联系电话',
    status          TINYINT      DEFAULT 1 COMMENT '状态：1=启用，0=停用',
    remark          VARCHAR(500) COMMENT '备注',
    create_by       VARCHAR(50)  COMMENT '创建人',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(50)  COMMENT '更新人',
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag        TINYINT      DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB COMMENT='仓库';

-- ============================================
-- 5. 基础数据 - 库区
-- ============================================
DROP TABLE IF EXISTS wms_warehouse_area;
CREATE TABLE wms_warehouse_area (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    area_name       VARCHAR(200) NOT NULL COMMENT '库区名称',
    area_code       VARCHAR(50)  NOT NULL COMMENT '库区编码',
    warehouse_id    BIGINT       NOT NULL COMMENT '所属仓库ID',
    area_type       VARCHAR(50)  COMMENT '库区类型：存储区、拣货区、暂存区等',
    remark          VARCHAR(500) COMMENT '备注',
    create_by       VARCHAR(50)  COMMENT '创建人',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(50)  COMMENT '更新人',
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag        TINYINT      DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB COMMENT='库区';

-- ============================================
-- 6. 基础数据 - 库位
-- ============================================
DROP TABLE IF EXISTS wms_kw;
CREATE TABLE wms_kw (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    kw_name         VARCHAR(200) NOT NULL COMMENT '库位名称',
    kw_code         VARCHAR(50)  NOT NULL COMMENT '库位编码',
    area_id         BIGINT       NOT NULL COMMENT '所属库区ID',
    warehouse_id    BIGINT       NOT NULL COMMENT '所属仓库ID',
    kw_type         VARCHAR(50)  COMMENT '库位类型',
    capacity        DECIMAL(18,2) COMMENT '容量',
    status          TINYINT      DEFAULT 1 COMMENT '状态：1=空闲，0=占用',
    remark          VARCHAR(500) COMMENT '备注',
    create_by       VARCHAR(50)  COMMENT '创建人',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(50)  COMMENT '更新人',
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag        TINYINT      DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB COMMENT='库位';

-- ============================================
-- 7. 基础数据 - 月台
-- ============================================
DROP TABLE IF EXISTS wms_plat;
CREATE TABLE wms_plat (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    plat_name       VARCHAR(200) NOT NULL COMMENT '月台名称',
    plat_code       VARCHAR(50)  NOT NULL COMMENT '月台编码',
    warehouse_id    BIGINT       COMMENT '所属仓库ID',
    plat_type       VARCHAR(50)  COMMENT '月台类型：收货、发货等',
    status          TINYINT      DEFAULT 1 COMMENT '状态：1=可用，0=不可用',
    remark          VARCHAR(500) COMMENT '备注',
    create_by       VARCHAR(50)  COMMENT '创建人',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(50)  COMMENT '更新人',
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag        TINYINT      DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB COMMENT='月台';

-- ============================================
-- 8. 基础数据 - 供应商
-- ============================================
DROP TABLE IF EXISTS wms_supply;
CREATE TABLE wms_supply (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    supply_name     VARCHAR(200) NOT NULL COMMENT '供应商名称',
    supply_code     VARCHAR(50)  COMMENT '供应商编码',
    contact_person  VARCHAR(50)  COMMENT '联系人',
    contact_phone   VARCHAR(20)  COMMENT '联系电话',
    address         VARCHAR(500) COMMENT '地址',
    status          TINYINT      DEFAULT 1 COMMENT '状态：1=启用，0=停用',
    remark          VARCHAR(500) COMMENT '备注',
    create_by       VARCHAR(50)  COMMENT '创建人',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(50)  COMMENT '更新人',
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag        TINYINT      DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB COMMENT='供应商';

-- ============================================
-- 9. 基础数据 - 客户
-- ============================================
DROP TABLE IF EXISTS wms_cus;
CREATE TABLE wms_cus (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    cus_name        VARCHAR(200) NOT NULL COMMENT '客户名称',
    cus_code        VARCHAR(50)  COMMENT '客户编码',
    contact_person  VARCHAR(50)  COMMENT '联系人',
    contact_phone   VARCHAR(20)  COMMENT '联系电话',
    address         VARCHAR(500) COMMENT '地址',
    status          TINYINT      DEFAULT 1 COMMENT '状态：1=启用，0=停用',
    remark          VARCHAR(500) COMMENT '备注',
    create_by       VARCHAR(50)  COMMENT '创建人',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(50)  COMMENT '更新人',
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag        TINYINT      DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB COMMENT='客户';

-- ============================================
-- 10. 基础数据 - 订单类型
-- ============================================
DROP TABLE IF EXISTS wms_order_type;
CREATE TABLE wms_order_type (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    type_name   VARCHAR(100) NOT NULL COMMENT '类型名称',
    type_code   VARCHAR(50)  NOT NULL COMMENT '类型编码',
    direction   VARCHAR(20)  COMMENT '方向：IN=入库，OUT=出库',
    remark      VARCHAR(500) COMMENT '备注',
    create_by   VARCHAR(50)  COMMENT '创建人',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(50)  COMMENT '更新人',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag    TINYINT      DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB COMMENT='订单类型';

-- ============================================
-- 11. 基础数据 - 部件类型
-- ============================================
DROP TABLE IF EXISTS wms_comtype;
CREATE TABLE wms_comtype (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    type_name   VARCHAR(100) NOT NULL COMMENT '类型名称',
    type_code   VARCHAR(50)  COMMENT '类型编码',
    parent_id   BIGINT       DEFAULT 0 COMMENT '父级ID',
    remark      VARCHAR(500) COMMENT '备注',
    create_by   VARCHAR(50)  COMMENT '创建人',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(50)  COMMENT '更新人',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag    TINYINT      DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB COMMENT='部件类型';

-- ============================================
-- 插入测试数据
-- ============================================
INSERT INTO wms_goods_type (type_name, type_code, parent_id, remark) VALUES
('电子元器件', 'ELEC', 0, '电子类元器件'),
('五金件', 'HARD', 0, '五金标准件'),
('包装材料', 'PACK', 0, '包装耗材');

INSERT INTO wms_unit (unit_name, unit_code) VALUES
('个', 'PCS'),
('箱', 'BOX'),
('托', 'PAL'),
('米', 'M'),
('千克', 'KG');

INSERT INTO wms_warehouse (warehouse_name, warehouse_code, address, contact_person, contact_phone, status) VALUES
('深圳总仓', 'SZ-MAIN', '深圳市宝安区XX路100号', '张三', '13800138000', 1),
('上海分仓', 'SH-BRANCH', '上海市浦东新区XX路200号', '李四', '13900139000', 1);
