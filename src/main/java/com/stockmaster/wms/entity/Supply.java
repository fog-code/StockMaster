package com.stockmaster.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("wms_supply")
@Schema(description = "供应商")
public class Supply {
    @TableId(type = IdType.AUTO) private Long id;
    @Schema(description = "供应商名称") private String supplyName;
    @Schema(description = "供应商编码") private String supplyCode;
    @Schema(description = "联系人") private String contactPerson;
    @Schema(description = "联系电话") private String contactPhone;
    @Schema(description = "地址") private String address;
    @Schema(description = "状态：1=启用，0=停用") private Integer status;
    @Schema(description = "备注") private String remark;
    @TableField(fill = FieldFill.INSERT) private String createBy;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE) private String updateBy;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updateTime;
    @TableLogic private Integer delFlag;
}
