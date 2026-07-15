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
    @TableId(type = IdType.AUTO) private Long id;
    @Schema(description = "入库单头ID") private Long headerId;
    @Schema(description = "入库单行ID") private Long lineId;
    @Schema(description = "物料ID") private Long goodsId;
    @Schema(description = "应检数量") private BigDecimal planQty;
    @Schema(description = "实检数量") private BigDecimal realQty;
    @Schema(description = "合格数量") private BigDecimal qualifiedQty;
    @Schema(description = "不合格数量") private BigDecimal unqualifiedQty;
    @Schema(description = "状态：待验收→验收中→已验收") private String status;
    @Schema(description = "备注") private String remark;
    @TableField(fill = FieldFill.INSERT) private String createBy;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE) private String updateBy;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updateTime;
    @TableLogic private Integer delFlag;

    // 关联名
    @TableField(exist = false) @Schema(description = "物料名称") private String goodsName;
    @TableField(exist = false) @Schema(description = "订单号") private String orderNo;
}
