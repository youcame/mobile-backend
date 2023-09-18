package com.mobile.mobilebackend.Authority;


import com.mobile.mobilebackend.common.ErrorCode;
import com.mobile.mobilebackend.exception.BusinessException;
import com.mobile.mobilebackend.model.domain.User;

import javax.servlet.http.HttpServletRequest;

import static com.mobile.mobilebackend.constant.UserConstant.*;


public class UserAuthority {
    public static final boolean isAdmin(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return ADMIN_ROLE == user.getUserRole();
    }

    public static final boolean isDefault(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return DEFAULT_ROLE == user.getUserRole();
    }

    public static final String getCurrentUserName(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return user.getUsername();
    }

    public static final String getCurrentUserAccount(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return user.getUserAccount();
    }

    public static final Long getCurrentUserId(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(user == null)throw new BusinessException(ErrorCode.NO_LOGIN);
        return user.getId();
    }

}
