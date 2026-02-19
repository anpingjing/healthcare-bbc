package com.healthcare.bbc.converter;

import com.healthcare.bbc.annotation.Desensitize;
import com.healthcare.bbc.entity.User;
import com.healthcare.bbc.util.DesensitizeUtil;
import com.healthcare.bbc.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserConverter {
    public UserVO toVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setGenderText(getGenderText(user.getGender()));
        vo.setStatusText(getStatusText(user.getStatus()));
        applyDesensitization(vo);
        return vo;
    }

    public List<UserVO> toVOList(List<User> users) {
        if (users == null || users.isEmpty()) {
            return List.of();
        }
        return users.stream().map(this::toVO).collect(Collectors.toList());
    }

    private void applyDesensitization(UserVO vo) {
        try {
            Field[] fields = UserVO.class.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Desensitize.class)) {
                    field.setAccessible(true);
                    String value = (String) field.get(vo);
                    if (value != null) {
                        Desensitize annotation = field.getAnnotation(Desensitize.class);
                        String desensitizedValue = switch (annotation.type()) {
                            case PHONE -> DesensitizeUtil.desensitizePhone(value);
                            case EMAIL -> DesensitizeUtil.desensitizeEmail(value);
                            case ID_CARD -> DesensitizeUtil.desensitizeIdCard(value);
                            case NAME -> DesensitizeUtil.desensitizeName(value);
                            case BANK_CARD -> DesensitizeUtil.desensitizeBankCard(value);
                            case ADDRESS -> DesensitizeUtil.desensitizeAddress(value);
                        };
                        field.set(vo, desensitizedValue);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("脱敏处理失败", e);
        }
    }

    private String getGenderText(Integer gender) {
        if (gender == null) {
            return "未知";
        }
        return switch (gender) {
            case 1 -> "男";
            case 2 -> "女";
            default -> "未知";
        };
    }

    private String getStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        return status == 1 ? "正常" : "禁用";
    }
}
