package com.mobile.mobilebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobile.mobilebackend.mapper.TagMapper;
import com.mobile.mobilebackend.model.domain.Tag;
import com.mobile.mobilebackend.service.TagService;
import org.springframework.stereotype.Service;

/**
* @author Huang
* @description 针对表【tag】的数据库操作Service实现
* @createDate 2023-07-28 15:15:41
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {

}




