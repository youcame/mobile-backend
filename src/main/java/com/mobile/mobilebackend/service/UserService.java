package com.mobile.mobilebackend.service;

import com.mobile.mobilebackend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mobile.mobilebackend.model.vo.UserVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Huang
* @description 针对表【user(用户信息表)】的数据库操作Service
* @createDate 2023-07-28 15:15:41
*/
public interface UserService extends IService<User> {
    /**
     *
     * @param userAccount
     * @param password
     * @param checkPassword
     * @return 用户id
     */
    long userRegister(String userAccount, String password, String checkPassword);

    /**
     * @param userAccount
     * @param password
     * @param request
     * @return 用户信息
     */
    User userLogin(String userAccount, String password, HttpServletRequest request);

    /**
     *
     * @param user
     * @return
     */
    User getSafeUser(User user);

    /**
     * @param request
     */
    int userLogout(HttpServletRequest request);

    /**
     *
     * @param user
     * @return
     */
    //boolean updateFrontUser(ModifyUserRequest user);

    /**
     *
     * @param tagList 传入的标签组
     * @return 标签对应的用户
     */
    public List<User> searchUserByTags(List<String> tagList);

    Boolean updateFrontUser(User user, User loginUser, HttpServletRequest request);

    public User getLoginUser(HttpServletRequest request);

    List<UserVo> matchUser(int num, User currentUser);
}
