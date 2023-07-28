package com.mobile.mobilebackend.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobile.mobilebackend.common.ErrorCode;
import com.mobile.mobilebackend.exception.BusinessException;
import com.mobile.mobilebackend.mapper.TagMapper;
import com.mobile.mobilebackend.model.domain.Tag;
import com.mobile.mobilebackend.service.TagService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Huang
* @description 针对表【tag】的数据库操作Service实现
* @createDate 2023-07-28 15:15:41
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {

    @Override
    public int searchUserByTags(List<String> tagList) {
        if(CollectionUtils.isEmpty(tagList)){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请传入参数");
        }
        return 0;
    }
}




