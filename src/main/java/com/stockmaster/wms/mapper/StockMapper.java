package com.stockmaster.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stockmaster.wms.entity.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StockMapper extends BaseMapper<Stock> {

    /**
     * 🔥 悲观锁：FOR UPDATE 查询库存
     * 用于上架/下架时防止并发问题
     */
    @Select("SELECT * FROM wms_stock WHERE goods_id = #{goodsId} AND kw_id = #{kwId} AND del_flag = 0 FOR UPDATE")
    Stock selectForUpdate(@Param("goodsId") Long goodsId, @Param("kwId") Long kwId);
}
