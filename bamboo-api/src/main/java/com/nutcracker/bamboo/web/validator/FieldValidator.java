package com.nutcracker.bamboo.web.validator;

import java.util.Arrays;

import com.nutcracker.bamboo.common.annotation.ValidField;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 字段校验器
 *
 * @author 胡桃夹子
 * @since 2024/11/18
 */
public class FieldValidator implements ConstraintValidator<ValidField, String> {

    private String[] allowedValues;

    @Override
    public void initialize(ValidField constraintAnnotation) {
        // 初始化允许的值列表
        this.allowedValues = constraintAnnotation.allowedValues();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            // 如果字段允许为空，可以返回 true
            return true;
        }
        // 检查值是否在允许列表中
        return Arrays.asList(allowedValues).contains(value);
    }
}
