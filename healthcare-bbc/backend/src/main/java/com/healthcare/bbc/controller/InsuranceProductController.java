package com.healthcare.bbc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthcare.bbc.common.Result;
import com.healthcare.bbc.dto.PageQueryDTO;
import com.healthcare.bbc.entity.InsuranceProduct;
import com.healthcare.bbc.service.InsuranceProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Tag(name = "保险产品管理", description = "保险产品信息管理")
@RestController
@RequestMapping("/api/v1/insurance-products")
@Validated
public class InsuranceProductController {

    @Autowired
    private InsuranceProductService insuranceProductService;

    @Operation(summary = "分页查询保险产品")
    @GetMapping
    public Result<Page<InsuranceProduct>> list(PageQueryDTO queryDTO) {
        Page<InsuranceProduct> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return Result.success(insuranceProductService.page(page));
    }

    @Operation(summary = "获取所有可用保险产品")
    @GetMapping("/available")
    public Result<List<InsuranceProduct>> getAvailableProducts() {
        return Result.success(insuranceProductService.getAvailableProducts());
    }

    @Operation(summary = "根据产品类型获取保险产品")
    @GetMapping("/type/{productType}")
    public Result<List<InsuranceProduct>> getByProductType(@PathVariable @NotNull String productType) {
        return Result.success(insuranceProductService.getByProductType(productType));
    }

    @Operation(summary = "根据ID获取保险产品")
    @GetMapping("/{id}")
    public Result<InsuranceProduct> getById(@PathVariable @NotNull Long id) {
        InsuranceProduct product = insuranceProductService.getById(id);
        if (product == null) {
            return Result.error("保险产品不存在");
        }
        return Result.success(product);
    }

    @Operation(summary = "创建保险产品")
    @PostMapping
    public Result<InsuranceProduct> create(@RequestBody InsuranceProduct product) {
        insuranceProductService.save(product);
        return Result.success(product);
    }

    @Operation(summary = "更新保险产品")
    @PutMapping("/{id}")
    public Result<InsuranceProduct> update(@PathVariable @NotNull Long id, @RequestBody InsuranceProduct product) {
        product.setProductId(id);
        insuranceProductService.updateById(product);
        return Result.success(product);
    }

    @Operation(summary = "删除保险产品")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.success(insuranceProductService.removeById(id));
    }
}
