package com.hmdp.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.hmdp.utils.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public Result sendCode(String phone, HttpSession session) {
        //1.校验手机号
        boolean phoneInvalid = RegexUtils.isPhoneInvalid(phone);
        if(phoneInvalid==false){
            return Result.fail("手机号格式不符合");
        }
        //2.生成验证码
        String code = RandomUtil.randomNumbers(6);
        //3.保存验证码到session
        session.setAttribute("code",code);
        //4.发送验证码
        log.debug("发送短信验证码成功，验证码为："+code);

        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {

        /**
         * 1.再次检验手机格式
         * 2.取得session中缓存的正确code与用户发来的code作对比
         * 3.根据手机号（唯一key）查询user
         * 4.为null则新建一个新用户，并将其保存到数据库
         * 5.将用户信息保存到session
         */
        String phone = loginForm.getPhone();

        if(RegexUtils.isPhoneInvalid(phone)==false){
            return Result.fail("手机号格式错误");
        }
        String code = loginForm.getCode();
        String cacheCode= (String) session.getAttribute("code");

        if(code==null||!code.equals(cacheCode)){
            return Result.fail("验证码错误");
        }

        User user = query().eq("phone", phone).one();
        if(user==null){
            user=creatUserWithPhone(phone);
        }
        session.setAttribute("user",user);
        return Result.ok();
    }

    private User creatUserWithPhone(String phone) {
        User user=new User();
        user.setPhone(phone);
        user.setNickName(USER_NICK_NAME_PREFIX+RandomUtil.randomString(10));
        save(user);

        return user;
    }
}
