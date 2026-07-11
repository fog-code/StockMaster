package com.stockmaster.wms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stockmaster.wms.entity.Comtype;
import com.stockmaster.wms.mapper.ComtypeMapper;
import com.stockmaster.wms.service.IComtypeService;
import org.springframework.stereotype.Service;

@Service
public class ComtypeServiceImpl extends ServiceImpl<ComtypeMapper, Comtype> implements IComtypeService {}
