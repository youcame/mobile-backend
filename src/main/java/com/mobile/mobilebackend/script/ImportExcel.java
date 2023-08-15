package com.mobile.mobilebackend.script;

import com.alibaba.excel.EasyExcel;

/**
 *
 * @author hp
 */
public class ImportExcel {
    public static void main(String[] args) {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName = "D:\\Code\\java\\mobile-backend\\src\\main\\resources\\123.xlsx";
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
        EasyExcel.read(fileName, UserInfo.class, new UserDataListener()).sheet().doRead();
    }
}
