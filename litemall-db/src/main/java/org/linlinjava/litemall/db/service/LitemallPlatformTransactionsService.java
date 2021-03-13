package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallPlatformTransactionsMapper;
import org.linlinjava.litemall.db.domain.LitemallPlatformTransactions;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class LitemallPlatformTransactionsService {

    @Resource
    private LitemallPlatformTransactionsMapper transactionsMapper;

    public int createTransaction (LitemallPlatformTransactions transaction) {
        transaction.setVersion(1);
        transaction.setAddTime(LocalDateTime.now());
        transaction.setUpdateTime(LocalDateTime.now());
        transaction.setDeleted(Boolean.FALSE);
        transaction.setStatus(1);
        transactionsMapper.insert(transaction);
        return transaction.getId();
    }
}
