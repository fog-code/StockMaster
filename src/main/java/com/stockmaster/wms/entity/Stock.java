package com.stockmaster.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("wms_stock")
@Schema(description = "库存")
public class Stock {
    @TableId(type = IdType.AUTO) private Long id;
    @Schema(description = "物料ID") private Long goodsId;
    @Schema(description = "仓库ID") private Long warehouseId;
    @Schema(description = "库区ID") private Long areaId;
    @Schema(description = "库位ID") private Long kwId;
    @Schema(description = "库存数量") private BigDecimal qty;
    @Schema(description = "备注") private String remark;
    @TableField(fill = FieldFill.INSERT) private String createBy;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE) private String updateBy;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updateTime;
    @TableLogic private Integer delFlag;

    @TableField(exist = false) private String goodsName;
    @TableField(exist = false) private String kwName;
    @TableField(exist = false) private String warehouseName;
}
