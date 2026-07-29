package com.stockmaster.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stockmaster.wms.entity.StockTake;

import java.math.BigDecimal;

public interface IStockTakeService extends IService<StockTake> {
    /** 创建暗盘（自动获取账面库存） */
    void createDarkTake(Long warehouseId);
    /** 创建明盘 */
    void createLightTake(Long warehouseId, String remark);
    /** 开始盘点：待盘点→盘点中 */
    void startTake(Long id);
    /** 填写实盘数量 + 自动计算差异 */
    void fillActual(Long id, BigDecimal actualQty);
    /** 完成盘点：盘点中→已完成 */
    void completeTake(Long id);
    /** 差异明细分页查询（diffQty != 0） */
    IPage<StockTake> pageDiff(Integer current, Integer size, String takeType);
}
