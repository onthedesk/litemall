package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallCoinRecordsMapper;
import org.linlinjava.litemall.db.domain.LitemallCoinRecords;
import org.linlinjava.litemall.db.domain.LitemallCoinRecords.Column;
import org.linlinjava.litemall.db.domain.LitemallCoinRecordsExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;

@Service
public class LitemallCoinRecordsService {
    @Resource
    private LitemallCoinRecordsMapper coinRecordsMapper;
    private Column[] columns = new Column[]{Column.id};

    public List<LitemallCoinRecords> querySelective (Integer userId) {
        LitemallCoinRecordsExample example = new LitemallCoinRecordsExample();
        example.or().andUserIdEqualTo(userId);
        return coinRecordsMapper.selectByExample(example);
    }
}
