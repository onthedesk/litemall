package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallPlatformAccountMapper;
import org.linlinjava.litemall.db.domain.LitemallPlatformAccount;
import org.linlinjava.litemall.db.domain.LitemallPlatformAccountExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;

@Service
public class LitemallPlatformAccountService {

    @Resource
    private LitemallPlatformAccountMapper accountMapper;

    public List<LitemallPlatformAccount> queryByInfo(String name, String idCard) {
        LitemallPlatformAccountExample example = new LitemallPlatformAccountExample();
        example.or().andIdcardNumberEqualTo(idCard).andRealNameEqualTo(name);
        return accountMapper.selectByExample(example);
    }

    public List<LitemallPlatformAccount> queryByUserId(Integer userId) {
        LitemallPlatformAccountExample example = new LitemallPlatformAccountExample();
        example.or().andUserIdEqualTo(userId);
        return accountMapper.selectByExample(example);
    }

    public void updateAccountsUserIdByInfo(Integer userId, String name, String idCard) {
        LitemallPlatformAccountExample example = new LitemallPlatformAccountExample();
        LitemallPlatformAccount account = new LitemallPlatformAccount();
        account.setUserId(userId);
        // 只绑定未被绑定的平台账户
        example.or().andIdcardNumberEqualTo(idCard).andRealNameEqualTo(name).andUserIdIsNull();
        accountMapper.updateByExampleSelective(account, example);
    }
}
