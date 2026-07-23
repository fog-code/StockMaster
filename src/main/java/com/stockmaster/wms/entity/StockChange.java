package com.stockmaster.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("wms_stock_change")
@Schema(description = "库存调整记录")
public class StockChange {
    @TableId(type = IdType.AUTO) private Long id;
    @Schema(description = "库存记录ID") private Long stockId;
    @Schema(description = "物料ID") private Long goodsId;
    @Schema(description = "库位ID") private Long kwId;
    @Schema(description = "调整类型") private String changeType;
    @Schema(description = "调整前数量") private BigDecimal beforeQty;
    @Schema(description = "调整后数量") private BigDecimal afterQty;
    @Schema(description = "变更数量") private BigDecimal changeQty;
    @Schema(description = "备注") private String remark;
    @Schema(description = "操作人") private String createBy;
    @Schema(description = "操作时间") private LocalDateTime createTime;

    @TableField(exist = false) private String goodsName;
    @TableField(exist = false) private String kwName;
}
