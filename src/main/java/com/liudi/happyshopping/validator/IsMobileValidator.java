package com.liudi.happyshopping.validator;

import com.liudi.happyshopping.utils.ValidatorUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {

    private boolean required = false;

    public void initialize(IsMobile constraintAnnotation){
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (required){
            return ValidatorUtil.isMobile(s);
        }
        else{
            if (s==null || s.isEmpty())
                return true;
            else
                return ValidatorUtil.isMobile(s);
        }
    }
}