package com.stockmaster.wms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * FIFO 库存推荐结果：每库位建议取多少
 */
@Data
@AllArgsConstructor
@Schema(description = "FIFO推荐明细")
public class StockRecommendVO {
    @Schema(description = "库存ID") private Long stockId;
    @Schema(description = "物料ID") private Long goodsId;
    @Schema(description = "物料名称") private String goodsName;
    @Schema(description = "库位ID") private Long kwId;
    @Schema(description = "库位名称") private String kwName;
    @Schema(description = "仓库ID") private Long warehouseId;
    @Schema(description = "仓库名称") private String warehouseName;
    @Schema(description = "可用库存") private BigDecimal availableQty;
    @Schema(description = "建议取货量") private BigDecimal pickQty;
}
