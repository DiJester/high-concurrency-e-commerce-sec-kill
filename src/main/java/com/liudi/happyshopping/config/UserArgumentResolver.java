package com.liudi.happyshopping.config;

import com.liudi.happyshopping.access.UserContext;
import com.liudi.happyshopping.domain.HappyShoppingUser;
import com.liudi.happyshopping.service.HappyShoppingUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    HappyShoppingUserService userService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz == HappyShoppingUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

        return UserContext.getUser();
    }

}
