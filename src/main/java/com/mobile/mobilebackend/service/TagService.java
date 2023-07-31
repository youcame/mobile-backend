package com.mobile.mobilebackend.service;

import com.mobile.mobilebackend.model.domain.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Huang
* @description 针对表【tag】的数据库操作Service
* @createDate 2023-07-28 15:15:41
*/
public interface TagService extends IService<Tag> {
    public int searchUserByTags(List<String> tagList);
}
