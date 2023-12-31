package com.mobile.mobilebackend.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobile.mobilebackend.common.ErrorCode;
import com.mobile.mobilebackend.exception.BusinessException;
import com.mobile.mobilebackend.mapper.UserMapper;
import com.mobile.mobilebackend.model.domain.Tag;
import com.mobile.mobilebackend.model.domain.User;
import com.mobile.mobilebackend.model.vo.UserVo;
import com.mobile.mobilebackend.service.UserService;
import com.mobile.mobilebackend.utils.RecommandUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mobile.mobilebackend.constant.TeamConstant.TOTAL_RECOMMEND_USER;
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

    @Resource
    RedisTemplate<String,Object> redisTemplate;


    /**
     * @param userAccount
     * @param password
     * @param checkPassword
     * @return 新用户id
     */

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
        //默认昵称为用户名
        user.setUsername(userAccount);
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
        BeanUtils.copyProperties(user,safeUser);
        return safeUser;
    }
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
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
                System.out.println(tag);
                if (!tags.contains(tag)) return false;
            }
            return true;
        }).map(this::getSafeUser).collect(Collectors.toList());
        return collect;
    }

    /**
     * user为修改之后的用户Id，loginUser为现在登陆的用户Id
     * @param user
     * @param loginUser
     * @param request
     * @return
     */
    @Override
    public Boolean updateFrontUser(User user, User loginUser, HttpServletRequest request) {
        long id = user.getId();
        if(id!=loginUser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User changedUser = this.getById(id);
        if(user.getTags()!=null) {
            String[] tagList = user.getTags().split(",");
            String result = JSON.toJSONString(tagList);
            user.setTags(result);
        }
        BeanUtils.copyProperties(user,changedUser);
        return this.updateById(changedUser);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(user == null)throw new BusinessException(ErrorCode.NO_AUTH);
        return user;
    }

    @Override
    public List<UserVo> matchUser(int num, User currentUser) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String redisKey = String.format("mobile:user:recommend:%s",currentUser.getId());
        List<UserVo> targetList= (List<UserVo>) valueOperations.get(redisKey);
        if(targetList==null){
            targetList = new ArrayList<>();
            String tags = currentUser.getTags();
            if(tags == null || "[]".equals(tags)){
                tags = "[\"男\",\"大二\",\"浑南校区\",\"网络开发\"]";
            }
            List<String> listTag = JSON.parseArray(tags,String.class);
            //当前登录用户的标签
            Collections.sort(listTag);
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.isNotNull("tags");
            queryWrapper.select("id", "userRole", "tags", "userName", "avatarUrl","profile");
            List<User> userList = this.list(queryWrapper);
            PriorityQueue<UserVo> queue = new PriorityQueue<>((a,b)->{
                    List<String> list1= JSON.parseArray(a.getTags(),String.class);
                    List<String> list2= JSON.parseArray(b.getTags(),String.class);
                    Collections.sort(list1);
                    Collections.sort(list2);
                    int dis1 = RecommandUtils.minTagDistance(list1,listTag);
                    int dis2 = RecommandUtils.minTagDistance(list2,listTag);
                    return dis2-dis1;
                }
            );
            for (User user : userList) {
                if(Objects.equals(user.getId(), currentUser.getId())||user.getUserRole()==1){
                    continue;
                }
                UserVo userVo = new UserVo();
                BeanUtils.copyProperties(user,userVo);
                queue.offer(userVo);
                if(queue.size()>TOTAL_RECOMMEND_USER) {
                    queue.poll();
                }
            }
            int n=queue.size();
            for(int i=0;i<n&&i<TOTAL_RECOMMEND_USER;i++){
                targetList.add(queue.poll());
            }
            valueOperations.set(redisKey,targetList,2, TimeUnit.HOURS);
        }
        Collections.shuffle(targetList);
        return targetList.subList(0,num);
    }

}




