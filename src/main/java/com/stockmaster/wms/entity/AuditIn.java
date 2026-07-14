package com.stockmaster.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("wms_audit_in")
@Schema(description = "入库质检记录")
public class AuditIn {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "入库单头ID")
    private Long headerId;

    @Schema(description = "入库单行ID")
    private Long lineId;

    @Schema(description = "物料ID")
    private Long goodsId;

    @Schema(description = "单位ID")
    private Long unitId;

    @Schema(description = "计划数量（来自入库单行）")
    private BigDecimal planQty;

    @Schema(description = "实际质检数量（质检时填写）")
    private BigDecimal realQty;

    @Schema(description = "状态：待质检/合格/不合格")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer delFlag;

    // ===== 关联名称（非表字段） =====

    @TableField(exist = false)
    @Schema(description = "物料名称")
    private String goodsName;

    @TableField(exist = false)
    @Schema(description = "物料编码")
    private String goodsCode;

    @TableField(exist = false)
    @Schema(description = "单位名称")
    private String unitName;

    @TableField(exist = false)
    @Schema(description = "入库单号")
    private String orderNo;
}
