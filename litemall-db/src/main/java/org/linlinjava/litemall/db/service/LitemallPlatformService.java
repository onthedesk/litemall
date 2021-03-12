package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallPlatformMapper;
import org.linlinjava.litemall.db.domain.LitemallPlatform;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LitemallPlatformService {
    @Resource
    private LitemallPlatformMapper platformMapper;

    public LitemallPlatform getPlatformById(Integer platformId) {
        return platformMapper.selectByPrimaryKey(platformId);
    }
}
