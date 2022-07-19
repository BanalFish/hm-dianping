package com.hmdp.utils;

import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * 对部分Controller进行拦截，完成登录校验功能
         * 1.从session中取出user，判断是否存在
         * 2.存在就保存到ThreadLocal
         * 3.没有就返回
         */
        HttpSession session = request.getSession();
        Object user = session.getAttribute("user");

        if(user==null){
            response.setStatus(401);
            return false;
        }
        UserHolder.saveUser((User) user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
