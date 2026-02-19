package com.healthcare.bbc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthcare.bbc.common.Result;
import com.healthcare.bbc.converter.UserConverter;
import com.healthcare.bbc.dto.PageQueryDTO;
import com.healthcare.bbc.dto.UserCreateDTO;
import com.healthcare.bbc.dto.UserUpdateDTO;
import com.healthcare.bbc.entity.User;
import com.healthcare.bbc.service.UserService;
import com.healthcare.bbc.util.ValidationUtil;
import com.healthcare.bbc.vo.PageResult;
import com.healthcare.bbc.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Tag(name = "用户管理", description = "用户基础信息 CRUD")
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserConverter userConverter;

    @Operation(summary = "分页查询用户列表")
    @GetMapping
    public Result<PageResult<UserVO>> list(PageQueryDTO queryDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (!ValidationUtil.isEmpty(queryDTO.getKeyword())) {
            wrapper.and(w -> w.like(User::getName, queryDTO.getKeyword())
                    .or().like(User::getPhone, queryDTO.getKeyword())
                    .or().like(User::getEmail, queryDTO.getKeyword()));
        }
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<User> resultPage = userService.page(page, wrapper);

        List<UserVO> voList = userConverter.toVOList(resultPage.getRecords());
        PageResult<UserVO> pageResult = PageResult.of(resultPage.getTotal(), voList, queryDTO.getPageNum(), queryDTO.getPageSize());
        return Result.success(pageResult);
    }

    @Operation(summary = "根据 ID 获取用户信息")
    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable @NotNull Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(userConverter.toVO(user));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public Result<UserVO> create(@Valid @RequestBody UserCreateDTO createDTO) {
        User user = new User();
        BeanUtils.copyProperties(createDTO, user);
        userService.save(user);
        return Result.success(userConverter.toVO(user));
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/{id}")
    public Result<UserVO> update(@PathVariable @NotNull Long id, @Valid @RequestBody UserUpdateDTO updateDTO) {
        User existUser = userService.getById(id);
        if (existUser == null) {
            return Result.error("用户不存在");
        }
        BeanUtils.copyProperties(updateDTO, existUser, "userId", "wechatId", "createTime");
        userService.updateById(existUser);
        return Result.success(userConverter.toVO(existUser));
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(userService.removeById(id));
    }
}
