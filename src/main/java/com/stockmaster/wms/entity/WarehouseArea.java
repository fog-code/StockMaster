package com.stockmaster.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_warehouse_area")
@Schema(description = "库区")
public class WarehouseArea {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "库区名称")
    private String areaName;

    @Schema(description = "库区编码")
    private String areaCode;

    @Schema(description = "所属仓库ID")
    private Long warehouseId;

    @Schema(description = "库区类型（存储区/拣货区/暂存区等）")
    private String areaType;

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

    @TableField(exist = false)
    @Schema(description = "仓库名称")
    private String warehouseName;
}
