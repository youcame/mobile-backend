package com.mobile.mobilebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobile.mobilebackend.service.tagService;
import com.mobile.mobilebackend.model.domain.tag;
import com.mobile.mobilebackend.mapper.tagMapper;
import org.springframework.stereotype.Service;

/**
* @author hp
* @description 针对表【tag】的数据库操作Service实现
* @createDate 2023-07-28 15:04:35
*/
@Service
public class tagServiceImpl extends ServiceImpl<tagMapper, tag>
    implements tagService {

}




