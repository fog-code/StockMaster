package com.stockmaster.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("wms_stock_take")
@Schema(description = "盘点单")
public class StockTake {
    @TableId(type = IdType.AUTO) private Long id;
    @Schema(description = "盘点单号") private String takeNo;
    @Schema(description = "仓库ID") private Long warehouseId;
    @Schema(description = "库区ID") private Long areaId;
    @Schema(description = "物料ID") private Long goodsId;
    @Schema(description = "库位ID") private Long kwId;
    @Schema(description = "账面数量") private BigDecimal bookQty;
    @Schema(description = "实盘数量") private BigDecimal actualQty;
    @Schema(description = "差异") private BigDecimal diffQty;
    @Schema(description = "盘点类型：明盘/暗盘") private String takeType;
    @Schema(description = "状态") private String status;
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
