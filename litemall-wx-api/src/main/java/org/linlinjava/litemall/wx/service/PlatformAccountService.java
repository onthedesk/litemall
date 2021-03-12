package org.linlinjava.litemall.wx.service;

import org.linlinjava.litemall.db.domain.LitemallPlatform;
import org.linlinjava.litemall.db.domain.LitemallPlatformAccount;
import org.linlinjava.litemall.db.service.LitemallPlatformAccountService;
import org.linlinjava.litemall.db.service.LitemallPlatformService;
import org.linlinjava.litemall.wx.dto.PlatformAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlatformAccountService {

    @Autowired
    private LitemallPlatformAccountService litemallPlatformAccountService;

    @Autowired
    private LitemallPlatformService litemallPlatformService;

    public List<PlatformAccount> queryByUserId(Integer userId) {
        List<LitemallPlatformAccount> litemallPlatformAccounts = litemallPlatformAccountService.queryByUserId(userId);
        return gatherPlatfomAccount(litemallPlatformAccounts);
    }

    public List<PlatformAccount> queryByInfo(String name, String idCard) {
        List<LitemallPlatformAccount> litemallPlatformAccounts = litemallPlatformAccountService.queryByInfo(name, idCard);
        return gatherPlatfomAccount(litemallPlatformAccounts);
    }

    private List<PlatformAccount> gatherPlatfomAccount(List<LitemallPlatformAccount> litemallPlatformAccounts) {

        List<PlatformAccount> accounts = new ArrayList<>();

        if (litemallPlatformAccounts == null || litemallPlatformAccounts.size() < 1) {
            return accounts;
        }

        for(int i = 0; i < litemallPlatformAccounts.size(); i++) {
            LitemallPlatformAccount platformAccount = litemallPlatformAccounts.get(i);
            PlatformAccount account = new PlatformAccount();
            LitemallPlatform platform = litemallPlatformService.getPlatformById(platformAccount.getPlatfromId());
            // todo 把算子作为配置项放到配置文件，所有的金额从数据库读取时除以10000
            Double operator = 10000.;
            account.setAvailableAmount(platformAccount.getAvailableAmount() / operator );
            account.setDueAmount(platformAccount.getDueAmount() / operator );
            account.setTotalAmount(platformAccount.getTotalAmount() / operator );
            account.setUnAvailableAmount(platformAccount.getUnavailableAmount() / operator );
            account.setUsedAmount(platformAccount.getUsedAmuont() / operator );
            account.setIdCardNumber(platformAccount.getIdcardNumber());
            account.setRealName(platformAccount.getRealName());
            account.setPlatformName(platform.getName());
            account.setPlatformPhone(platform.getPhone());
            account.setMobile(platformAccount.getMobile());

            accounts.add(account);
        }

        return accounts;
    }
}
