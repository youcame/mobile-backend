package com.mobile.mobilebackend.script;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hp
 */
@Data
@EqualsAndHashCode
public class UserInfo {

    /**
     * 用户Id
     */
    @ExcelProperty("用户Id")
    private Long id;

    /**
     * 用户昵称
     */
    @ExcelProperty("用户昵称")
    private String username;
    /**
     * 用户账户
     */
    @ExcelProperty("用户账户")
    private String string;
}
