package com.healthcare.bbc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthcare.bbc.entity.InsuranceProduct;
import com.healthcare.bbc.mapper.InsuranceProductMapper;
import com.healthcare.bbc.service.InsuranceProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InsuranceProductServiceImpl extends ServiceImpl<InsuranceProductMapper, InsuranceProduct> implements InsuranceProductService {
    @Override
    public List<InsuranceProduct> getByProductType(String productType) {
        LambdaQueryWrapper<InsuranceProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InsuranceProduct::getProductType, productType);
        wrapper.eq(InsuranceProduct::getStatus, 1);
        wrapper.orderByDesc(InsuranceProduct::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<InsuranceProduct> getAvailableProducts() {
        LambdaQueryWrapper<InsuranceProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InsuranceProduct::getStatus, 1);
        wrapper.orderByDesc(InsuranceProduct::getCreateTime);
        return list(wrapper);
    }
}
