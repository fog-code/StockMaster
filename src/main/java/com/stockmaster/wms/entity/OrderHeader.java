package com.stockmaster.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("wms_order_header")
@Schema(description = "订单头")
public class OrderHeader {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "订单号（自动生成）")
    private String orderNo;

    @Schema(description = "订单类型ID")
    private Long orderTypeId;

    @Schema(description = "仓库ID")
    private Long warehouseId;

    @Schema(description = "供应商/客户ID")
    private Long supplyId;

    @Schema(description = "订单日期")
    private LocalDate orderDate;

    @Schema(description = "状态：待验收→验收中→已验收→已完成")
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

    // 关联名称（非表字段）
    @TableField(exist = false)
    @Schema(description = "仓库名称")
    private String warehouseName;

    @TableField(exist = false)
    @Schema(description = "供应商名称")
    private String supplyName;

    @TableField(exist = false)
    @Schema(description = "订单类型名称")
    private String orderTypeName;

    @TableField(exist = false)
    @Schema(description = "子表明细")
    private java.util.List<OrderLine> lines;
}
