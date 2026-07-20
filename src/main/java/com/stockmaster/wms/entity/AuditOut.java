package com.stockmaster.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("wms_audit_out")
@Schema(description = "出库下架记录")
public class AuditOut {
    @TableId(type = IdType.AUTO) private Long id;
    @Schema(description = "出库单头ID") private Long headerId;
    @Schema(description = "出库单行ID") private Long lineId;
    @Schema(description = "物料ID") private Long goodsId;
    @Schema(description = "库存记录ID") private Long stockId;
    @Schema(description = "库位ID") private Long kwId;
    @Schema(description = "计划下架数量") private BigDecimal planQty;
    @Schema(description = "实际下架数量") private BigDecimal realQty;
    @Schema(description = "状态") private String status;
    @Schema(description = "备注") private String remark;
    @TableField(fill = FieldFill.INSERT) private String createBy;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE) private String updateBy;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updateTime;
    @TableLogic private Integer delFlag;

    @TableField(exist = false) private String goodsName;
    @TableField(exist = false) private String kwName;
    @TableField(exist = false) private String orderNo;
}
