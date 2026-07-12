package com.stockmaster.wms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stockmaster.wms.entity.OrderLine;
import com.stockmaster.wms.mapper.OrderLineMapper;
import com.stockmaster.wms.service.IOrderLineService;
import org.springframework.stereotype.Service;

@Service
public class OrderLineServiceImpl extends ServiceImpl<OrderLineMapper, OrderLine> implements IOrderLineService {}
