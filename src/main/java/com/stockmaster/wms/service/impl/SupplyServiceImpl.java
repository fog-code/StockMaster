package com.stockmaster.wms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stockmaster.wms.entity.Supply;
import com.stockmaster.wms.mapper.SupplyMapper;
import com.stockmaster.wms.service.ISupplyService;
import org.springframework.stereotype.Service;

@Service
public class SupplyServiceImpl extends ServiceImpl<SupplyMapper, Supply> implements ISupplyService {}
