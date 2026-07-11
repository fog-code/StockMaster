package com.stockmaster.wms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stockmaster.wms.entity.Cus;
import com.stockmaster.wms.mapper.CusMapper;
import com.stockmaster.wms.service.ICusService;
import org.springframework.stereotype.Service;

@Service
public class CusServiceImpl extends ServiceImpl<CusMapper, Cus> implements ICusService {}
