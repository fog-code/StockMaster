package com.stockmaster.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 仓库实体
 */
@Data
@TableName("wms_warehouse")
@Schema(description = "仓库")
public class Warehouse {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "仓库名称")
    private String warehouseName;

    @Schema(description = "仓库编码")
    private String warehouseCode;

    @Schema(description = "仓库地址")
    private String address;

    @Schema(description = "联系人")
    private String contactPerson;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "状态：1=启用，0=停用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新人")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(description = "逻辑删除：0=未删除，1=已删除")
    private Integer delFlag;
}
