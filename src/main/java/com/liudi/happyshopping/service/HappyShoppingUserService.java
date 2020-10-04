package com.liudi.happyshopping.service;

import com.liudi.happyshopping.controller.LoginController;
import com.liudi.happyshopping.dao.HappyShoppingUserDao;
import com.liudi.happyshopping.domain.HappyShoppingUser;
import com.liudi.happyshopping.exception.GlobalException;
import com.liudi.happyshopping.redis.RedisService;
import com.liudi.happyshopping.redis.SeckillUserKey;
import com.liudi.happyshopping.result.CodeMsg;
import com.liudi.happyshopping.utils.MD5Util;
import com.liudi.happyshopping.utils.UUIDUtil;
import com.liudi.happyshopping.vo.LoginVo;
import com.liudi.happyshopping.vo.SignupVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class HappyShoppingUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    HappyShoppingUserDao happyShoppingUserDao;

    @Autowired
    RedisService redisService;

    public HappyShoppingUser getById(long id){
        HappyShoppingUser user = redisService.get(SeckillUserKey.getById,String.valueOf(id),HappyShoppingUser.class);
        if (user!=null)
            return user;
        user = happyShoppingUserDao.getById(id);
        if (user!=null)
            redisService.set(SeckillUserKey.getById,String.valueOf(id),user);
        return user;
    }

    public boolean updatePassword(String token,long id,String formPass){
        HappyShoppingUser user = getById(id);
        if (user==null)
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        HappyShoppingUser newUser = new HappyShoppingUser();
        newUser.setId(id);
        newUser.setPassword(MD5Util.formPassToServerPass(user.getSalt(),formPass));
        happyShoppingUserDao.update(newUser);
        redisService.delete(SeckillUserKey.getById,String.valueOf(id));
        user.setPassword(newUser.getPassword());
        redisService.set(SeckillUserKey.token,token,user);
        return true;
    }

    public boolean signup(HttpServletResponse response, SignupVo signupVo){
        System.out.println("Service signUp:");
        if(signupVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = signupVo.getMobile();
        HappyShoppingUser user = getById(Long.parseLong(mobile));
        if (user!=null) {
            throw new GlobalException(CodeMsg.MOBILE_ALREADY_USED);
        }
        System.out.println("Service signUp:"+signupVo.toString());
        HappyShoppingUser newUser = new HappyShoppingUser();
        newUser.setId(Long.parseLong(mobile));
        newUser.setNickname(signupVo.getNickname());
        newUser.setSalt(MD5Util.getSalt());
        newUser.setPassword(MD5Util.formPassToServerPass(MD5Util.getSalt(),signupVo.getPassword()));
        newUser.setGender(signupVo.getGender());
        happyShoppingUserDao.insert(newUser);
        String token = UUIDUtil.uuid();
        //redisService.set(SeckillUserKey.token,token,newUser);
        addCookie(token,newUser,response);
        return true;
    }
    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo==null)
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //mobile exist
        HappyShoppingUser user = getById(Long.parseLong(mobile));
        if (user==null)
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        //password
        String dbPass = user.getPassword();
        String salt = user.getSalt();
        String curPass = MD5Util.formPassToServerPass(salt,formPass);
        if (!curPass.equals(dbPass))
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        //generate cookie
        String token = UUIDUtil.uuid();
        addCookie(token,user,response);
        return true;
    }

    public HappyShoppingUser getByToken(HttpServletResponse response,String token) {
        if (token==null || token.isEmpty())
            return null;
        HappyShoppingUser user = redisService.get(SeckillUserKey.token,token,HappyShoppingUser.class);
        //extend session
        if (user!=null)
            addCookie(token,user,response);
        return user;
    }

    /**
     * Extend expire time in session and cookie
     * @param user todo
     * @param response todo
     */
    private void addCookie(String token,HappyShoppingUser user,HttpServletResponse response){
        redisService.set(SeckillUserKey.token,token,user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(SeckillUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}