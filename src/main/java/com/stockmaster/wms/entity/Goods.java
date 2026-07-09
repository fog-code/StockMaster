package com.stockmaster.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_goods")
@Schema(description = "物料")
public class Goods {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "物料名称")
    private String goodsName;

    @Schema(description = "物料编码")
    private String goodsCode;

    @Schema(description = "物料分类ID")
    private Long goodsTypeId;

    @Schema(description = "单位ID")
    private Long unitId;

    @Schema(description = "规格")
    private String spec;

    @Schema(description = "型号")
    private String model;

    @Schema(description = "条码")
    private String barcode;

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

    // ========== 非表字段（关联查询用） ==========

    @TableField(exist = false)
    @Schema(description = "物料分类名称")
    private String goodsTypeName;

    @TableField(exist = false)
    @Schema(description = "单位名称")
    private String unitName;
}
