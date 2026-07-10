package com.stockmaster.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_plat")
@Schema(description = "月台")
public class Plat {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "月台名称")
    private String platName;

    @Schema(description = "月台编码")
    private String platCode;

    @Schema(description = "所属仓库ID")
    private Long warehouseId;

    @Schema(description = "月台类型（收货/发货等）")
    private String platType;

    @Schema(description = "状态：1=可用，0=不可用")
    private Integer status;

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
