package com.healthcare.bbc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthcare.bbc.entity.InsuranceProduct;

import java.util.List;

public interface InsuranceProductService extends IService<InsuranceProduct> {
    List<InsuranceProduct> getByProductType(String productType);
    List<InsuranceProduct> getAvailableProducts();
}
