package com.stockmaster.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("wms_order_type")
@Schema(description = "订单类型")
public class OrderType {
    @TableId(type = IdType.AUTO) private Long id;
    @Schema(description = "类型名称") private String typeName;
    @Schema(description = "类型编码") private String typeCode;
    @Schema(description = "方向：IN=入库，OUT=出库") private String direction;
    @Schema(description = "备注") private String remark;
    @TableField(fill = FieldFill.INSERT) private String createBy;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE) private String updateBy;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updateTime;
    @TableLogic private Integer delFlag;
}
