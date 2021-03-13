package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallCoinAccountMapper;
import org.linlinjava.litemall.db.domain.LitemallCoinAccount;
import org.linlinjava.litemall.db.domain.LitemallCoinAccount.Column;
import org.linlinjava.litemall.db.domain.LitemallCoinAccountExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class LitemallCoinAccountService {
    @Resource
    private LitemallCoinAccountMapper coinAccountMapper;
    private Column[] columns = new Column[]{Column.id, Column.userId, Column.endTime, Column.availableAmount, Column.totalAmount};

    public LitemallCoinAccount getCoinAccountByUserIdSelecttive(Integer userId) {
        LitemallCoinAccountExample example = new LitemallCoinAccountExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return coinAccountMapper.selectOneByExampleSelective(example, columns);
    }

    public LitemallCoinAccount getCoinAccountByUserId(Integer userId) {
        LitemallCoinAccountExample example = new LitemallCoinAccountExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return coinAccountMapper.selectOneByExample(example);
    }

    public int createCoinAccount(LitemallCoinAccount account) {
        account.setVersion(1);
        account.setStatus(Boolean.FALSE);
        account.setAddTime(LocalDateTime.now());
        account.setUpdateTime(LocalDateTime.now());
        account.setDeleted(Boolean.FALSE);
        return coinAccountMapper.insert(account);
    }

    public int updateCoinAccount(LitemallCoinAccount account) {
        LitemallCoinAccountExample example = new LitemallCoinAccountExample();
        account.setVersion(account.getVersion() + 1);
        account.setUpdateTime(LocalDateTime.now());
        example.or().andIdEqualTo(account.getId());
        return coinAccountMapper.updateByExampleSelective(account, example);
    }
}
