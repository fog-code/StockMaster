package com.stockmaster.wms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stockmaster.wms.entity.OrderType;
import com.stockmaster.wms.mapper.OrderTypeMapper;
import com.stockmaster.wms.service.IOrderTypeService;
import org.springframework.stereotype.Service;

@Service
public class OrderTypeServiceImpl extends ServiceImpl<OrderTypeMapper, OrderType> implements IOrderTypeService {}
