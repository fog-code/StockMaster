package com.stockmaster.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stockmaster.wms.entity.OrderHeader;
import com.stockmaster.wms.entity.OrderLine;
import com.stockmaster.wms.mapper.OrderHeaderMapper;
import com.stockmaster.wms.service.IOrderHeaderService;
import com.stockmaster.wms.service.IOrderLineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class OrderHeaderServiceImpl extends ServiceImpl<OrderHeaderMapper, OrderHeader>
        implements IOrderHeaderService {

    private final IOrderLineService orderLineService;

    /**
     * 生成订单号：yyyyMMdd + 4位序号
     * 例：202607110001
     */
    @Override
    public String generateOrderNo() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 查今天最大的订单号
        LambdaQueryWrapper<OrderHeader> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(OrderHeader::getOrderNo, today);
        wrapper.orderByDesc(OrderHeader::getOrderNo);
        wrapper.last("LIMIT 1");

        OrderHeader last = this.getOne(wrapper, false);
        if (last == null || last.getOrderNo() == null) {
            return today + "0001";
        }
        // 取后4位递增
        String suffix = last.getOrderNo().substring(8); // 截取 yyyyMMdd 之后
        int seq = Integer.parseInt(suffix) + 1;
        return today + String.format("%04d", seq);
    }

    /**
     * 主子表联动保存（事务）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveWithLines(OrderHeader header) {
        // 1. 自动生成订单号
        if (header.getOrderNo() == null || header.getOrderNo().isEmpty()) {
            header.setOrderNo(generateOrderNo());
        }
        // 2. 默认状态
        if (header.getStatus() == null) {
            header.setStatus("待验收");
        }
        if (header.getOrderDate() == null) {
            header.setOrderDate(LocalDate.now());
        }

        // 3. 保存订单头
        this.save(header);

        // 4. 保存订单行（设置 header_id）
        if (header.getLines() != null && !header.getLines().isEmpty()) {
            for (OrderLine line : header.getLines()) {
                line.setHeaderId(header.getId());
                if (line.getStatus() == null) {
                    line.setStatus("待验收");
                }
            }
            orderLineService.saveBatch(header.getLines());
        }
        return true;
    }
}
