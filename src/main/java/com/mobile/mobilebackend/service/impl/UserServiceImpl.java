package com.mobile.mobilebackend.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobile.mobilebackend.common.ErrorCode;
import com.mobile.mobilebackend.exception.BusinessException;
import com.mobile.mobilebackend.mapper.UserMapper;
import com.mobile.mobilebackend.model.domain.User;
import com.mobile.mobilebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mobile.mobilebackend.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author Huang
* @description 针对表【user(用户信息表)】的数据库操作Service实现
* @createDate 2023-07-28 15:15:41
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    /**
     * 盐值
     */
    private static final String SALT = "hao";
    @Resource
    private UserMapper userMapper;

    /**
     * @param userAccount
     * @param password
     * @param checkPassword
     * @return 新用户id
     */

    //todo：修改自定义异常
    @Override
    public long userRegister(String userAccount, String password, String checkPassword) {

        // 1.校验
        //校验输入是否合法
        if (StringUtils.isAnyBlank(userAccount, password, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR,"参数不能为空哦~");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名至少需要四位哦~");
        }
        if (password.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户密码至少有8位哦~");
        }
        //账户不能包含特殊字符
        String validPattern = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR,"账号不能包含特殊字符哦~");
        }
        //检验两次密码是否相同
        if (!password.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR,"两次密码不相同");
        }
        //检验是否有重复账户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.ACCOUNT_SAME, "账户已经被注册过了~");
        }

        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes(StandardCharsets.UTF_8));
        System.out.println(encryptPassword);

        //3.插入数据
        queryWrapper = new QueryWrapper<>();
        count = userMapper.selectCount(queryWrapper);
        User user = new User();
        user.setUserAccount(userAccount);
        user.setPassword(encryptPassword);
        user.setId(count+1);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        boolean result = this.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAM_ERROR,"");
        }
        //todo:这里注册得到的id存在问题
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String password, HttpServletRequest request) {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount, password)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR,"参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAM_ERROR,"账号长度过短");
        }
        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.PARAM_ERROR,"密码长度过短");
        }
        //账户不能包含特殊字符
        String validPattern = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR,"账户包含特殊字符");
        }
        //加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("password", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"用户为空");
        }
        User safeUser = this.getSafeUser(user);
        //记录登录态,传入的是一个user数据！！！
        request.getSession().setAttribute(USER_LOGIN_STATE,safeUser);
        return safeUser;
    }

    @Override
    public User getSafeUser(User user){
        if(user == null){
            return null;

        }
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setGender(user.getGender());
        safeUser.setPhone(user.getPhone());
        safeUser.setEmail(user.getEmail());
        safeUser.setUserStatus(0);
        safeUser.setCreateTime(new Date());
        safeUser.setUserRole(user.getUserRole());
        return safeUser;
    }
    @Override
    public int userLogout(HttpServletRequest request) {
        request.removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public List<User> searchUserByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "不允许传入参数为空");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
          //数据库查询
//        for (String tagName : tagNameList) {
//            queryWrapper = queryWrapper.like("tags",tagName);
//        }
//        List<User> userList = userMapper.selectList(queryWrapper);
//        return userList.stream().map(this::getSafeUser).collect(Collectors.toList());
        //内存查询
        List<User> allUsers = userMapper.selectList(queryWrapper);
        List<User> collect = allUsers.stream().filter(user -> {
            if (user.getTags() == null) return false;
            List<String> tags = JSON.parseArray(user.getTags(), String.class);
            for (String tag : tagNameList) {
                if (!tags.contains(tag)) return false;
            }
            return true;
        }).map(this::getSafeUser).collect(Collectors.toList());
        return collect;
    }


//    curl "https://api.bilibili.com/x/v2/reply/main?csrf=321a53901454d3e6d480532346a101ef&mode=3&oid=787158194&pagination_str=^%^7B^%^22offset^%^22:^%^22^%^7B^%^5C^%^22type^%^5C^%^22:1,^%^5C^%^22direction^%^5C^%^22:1,^%^5C^%^22session_id^%^5C^%^22:^%^5C^%^221732673324414857^%^5C^%^22,^%^5C^%^22data^%^5C^%^22:^%^7B^%^7D^%^7D^%^22^%^7D&plat=1&type=1" ^
//            -H "authority: api.bilibili.com" ^
//            -H "accept: application/json, text/plain, */*" ^
//            -H "accept-language: zh-CN,zh;q=0.9" ^
//            -H "cookie: buvid3=203EB3D4-FCFE-0A50-93CF-6C0188F3842378544infoc; b_nut=1689123578; i-wanna-go-back=-1; _uuid=D536E75D-587D-7293-1983-DDA10E556A4B980690infoc; FEED_LIVE_VERSION=V8; header_theme_version=CLOSE; buvid4=3AE3AF37-7C65-29BD-07A0-851A322674BF79156-023071208-SlDOxG3np^%^2FcKl061wPP74w^%^3D^%^3D; DedeUserID=234120375; DedeUserID__ckMd5=478694db74f4f355; CURRENT_FNVAL=4048; nostalgia_conf=-1; rpdid=^|(RYluk^|k^|m0J'uY)muRl^|m~; b_ut=5; hit-new-style-dyn=1; hit-dyn-v2=1; buvid_fp_plain=undefined; LIVE_BUVID=AUTO7516899318942979; fingerprint=26d08b2e0b114a314510bfab85bbaf6c; PVID=1; CURRENT_QUALITY=64; SESSDATA=b3e5c002^%^2C1707384852^%^2Ce0c2d^%^2A82KuTSKzfRj0sfYoXyVq_3peBTZ2KH87EzKR4gRQSJXNXrT2Ig1g5rrBojMmKQdhjlLu79DAAASwA; bili_jct=321a53901454d3e6d480532346a101ef; sid=6xc28940; home_feed_column=5; browser_resolution=1920-923; buvid_fp=26d08b2e0b114a314510bfab85bbaf6c; b_lsid=D1AFA4A7_189F6DC6625; bp_video_offset_234120375=829875299187425408; bili_ticket=eyJhbGciOiJFUzM4NCIsImtpZCI6ImVjMDIiLCJ0eXAiOiJKV1QifQ.eyJleHAiOjE2OTIzMjI5OTMsImlhdCI6MTY5MjA2Mzc5MywicGx0IjotMX0.SO7WgTzH3PiMuKWo-9oK2vwWohJEvRDUPJILE97Kt9lATld5VH0unTilcz-HCTRq-hX353S16PtrIUkCUE2o1BLgBWkouJJJ6eqodLydN9JywfJGRAv7cuz75x343S-t; bili_ticket_expires=1692322993" ^
//            -H "origin: https://www.bilibili.com" ^
//            -H "referer: https://www.bilibili.com/video/BV1D14y1q7HA/?spm_id_from=333.1007.tianma.1-2-2.click&vd_source=ce2d296140a2f6de60b84bd36bb33b44" ^
//            -H "sec-ch-ua: ^\^"Not/A)Brand^\^";v=^\^"99^\^", ^\^"Google Chrome^\^";v=^\^"115^\^", ^\^"Chromium^\^";v=^\^"115^\^"" ^
//            -H "sec-ch-ua-mobile: ?0" ^
//            -H "sec-ch-ua-platform: ^\^"Windows^\^"" ^
//            -H "sec-fetch-dest: empty" ^
//            -H "sec-fetch-mode: cors" ^
//            -H "sec-fetch-site: same-site" ^
//            -H "user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36" ^
//            --compressed




















//    @Override
//    public boolean updateFrontUser(ModifyUserRequest user) {
//        long id = user.getId();
//        User changedUser = this.getById(id);
//        changedUser.setUserRole(user.getUserRole());
//        changedUser.setAvatarUrl(user.getAvatarUrl());
//        changedUser.setUserStatus(user.getUserStatus());
//        changedUser.setEmail(user.getEmail());
//        changedUser.setPhone(user.getPhone());
//        changedUser.setUsername(user.getUsername());
//        changedUser.setGender(user.getGender());
//        return this.updateById(changedUser);
//    }

}




