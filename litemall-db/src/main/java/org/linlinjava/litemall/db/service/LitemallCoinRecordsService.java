package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallCoinRecordsMapper;
import org.linlinjava.litemall.db.domain.LitemallCoinRecords;
import org.linlinjava.litemall.db.domain.LitemallCoinRecords.Column;
import org.linlinjava.litemall.db.domain.LitemallCoinRecordsExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.time.LocalDateTime;
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

    public int createCoinRecord(LitemallCoinRecords coinRecord) {
        coinRecord.setVersion(1);
        // 默认为已确认，若需要审核时，需要增加配置选型
        // todo 兑换记录是否需要审核
        coinRecord.setStatus(1);
        coinRecord.setAddTime(LocalDateTime.now());
        coinRecord.setUpdateTime(LocalDateTime.now());
        coinRecord.setDeleted(Boolean.FALSE);
        return coinRecordsMapper.insert(coinRecord);
    }
}
