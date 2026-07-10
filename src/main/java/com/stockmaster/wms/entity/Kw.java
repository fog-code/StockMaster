package com.stockmaster.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("wms_kw")
@Schema(description = "库位")
public class Kw {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "库位名称")
    private String kwName;

    @Schema(description = "库位编码")
    private String kwCode;

    @Schema(description = "所属库区ID")
    private Long areaId;

    @Schema(description = "所属仓库ID")
    private Long warehouseId;

    @Schema(description = "库位类型")
    private String kwType;

    @Schema(description = "容量")
    private BigDecimal capacity;

    @Schema(description = "状态：1=空闲，0=占用")
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

    // 非表字段
    @TableField(exist = false)
    @Schema(description = "库区名称")
    private String areaName;

    @TableField(exist = false)
    @Schema(description = "仓库名称")
    private String warehouseName;
}
