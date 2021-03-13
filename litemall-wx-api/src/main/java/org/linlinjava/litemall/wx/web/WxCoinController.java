package org.linlinjava.litemall.wx.web;

import io.swagger.models.auth.In;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallCoinAccount;
import org.linlinjava.litemall.db.domain.LitemallCoinRecords;
import org.linlinjava.litemall.db.domain.LitemallPlatformAccount;
import org.linlinjava.litemall.db.domain.LitemallPlatformTransactions;
import org.linlinjava.litemall.db.service.LitemallCoinAccountService;
import org.linlinjava.litemall.db.service.LitemallCoinRecordsService;
import org.linlinjava.litemall.db.service.LitemallPlatformAccountService;
import org.linlinjava.litemall.db.service.LitemallPlatformTransactionsService;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.dto.PlatformAccount;
import org.linlinjava.litemall.wx.service.CaptchaCodeManager;
import org.linlinjava.litemall.wx.service.PlatformAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

import static org.linlinjava.litemall.wx.util.WxResponseCode.AUTH_CAPTCHA_UNMATCH;

/**
 * 积分服务
 */
@RestController
@RequestMapping("/wx/coin")
@Validated
public class WxCoinController {
    private final Log Logger = LogFactory.getLog(WxCoinController.class);

    @Autowired
    private LitemallCoinRecordsService coinRecordsService;

    @Autowired
    private LitemallCoinAccountService coinAccountService;

    @Autowired
    private LitemallPlatformAccountService litemallPlatformAccountService;

    @Autowired
    private PlatformAccountService platformAccountService;

    @Autowired
    private LitemallPlatformTransactionsService transactionsService;

    /**
     *
     * 获取用户积分列表
     *
     * @param userId
     * @return 用户个人积分列表
     */

    @GetMapping("list")
    public Object list(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        Map<Object, Object> data = new HashMap<Object, Object>();
        data.put("list", coinRecordsService.querySelective(userId));
        return ResponseUtil.ok(data);
    }

    /**
     *
     * 获取用户积分账户
     *
     * @param userId
     * @return 用户积分账户列表
     *
     */

    @PostMapping("account")
    public Object account(@LoginUser Integer userId, @RequestBody String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        String name = JacksonUtil.parseString(body, "name");
        String idCard = JacksonUtil.parseString(body, "idCard");

        // 查询用户名下是否有账户，有账户，返回实名认证信息及账户信息
        LitemallCoinAccount account = coinAccountService.getCoinAccountByUserIdSelecttive(userId);

        Map<Object, Object> data = new HashMap<Object, Object>();

        if (account != null) {
            // todo 如果用户绑定完账户之后，后期又在平台账户中添加了用户身份证号对应的账户时，该方法无法获取到所有平台列表
            List<PlatformAccount> accountList = platformAccountService.queryByUserId(userId);
            data.put("accounts", accountList);
            return ResponseUtil.ok(data);
        }

        // 没有账户，且没有传入姓名及身份证号，则需要实名
        if (account == null && (name == null || idCard == null)) {
            return ResponseUtil.fail(751, "请先完成实名认证！");
        }

        // 没有账户，但是传入了姓名及身份证号，则通过实名信息进行查询，查到之后进行绑定
        if (account == null && (name != null && idCard != null))  {
            List<PlatformAccount> accountList = platformAccountService.queryByInfo(name, idCard);

            if (accountList.size() > 0) {

                // todo 需要判断是否已被其他用户绑定，若绑定则不允许重新绑定

                // todo 查询到的账户1被其他用户绑定，查询到的账户2未被绑定，这种情况也不允许绑定

                // 进行绑定
                litemallPlatformAccountService.updateAccountsUserIdByInfo(userId, name, idCard);

                // 创建空白积分账户，默认有效期三年
                createBlankCoinAccountByUserId(userId);

                // 返回账户列表
                data.put("accounts", accountList);
                return ResponseUtil.ok(data);
            }  else {
                // 查询不到账户，则返回未查询到名下账户，请联系客服

                return ResponseUtil.fail(752, "没有查询到账户，请您确认个人信息或联系客服");
            }

        }

        return ResponseUtil.fail(753, "业务处理失败");

    }

    /**
     *
     * 积分兑换
     *
     *
     */

    @PostMapping("/exchange")
    public Object exchange(@LoginUser Integer userId, @RequestBody String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        String amount = JacksonUtil.parseString(body, "amount");
        String mobile = JacksonUtil.parseString(body, "mobile");
        String code = JacksonUtil.parseString(body, "code");
        Integer accountId = JacksonUtil.parseInteger(body, "accountId");

        if (amount == null || code == null || mobile == null || accountId == null) {
            return ResponseUtil.badArgument();
        }

        //判断验证码是否正确
//        String cacheCode = CaptchaCodeManager.getCachedCaptcha(mobile);
//        if (cacheCode == null || cacheCode.isEmpty() || !cacheCode.equals(code)) {
//            return ResponseUtil.fail(AUTH_CAPTCHA_UNMATCH, "验证码错误");
//        }
//
//        if (cacheCode.equalsIgnoreCase("-1")) {
//            return ResponseUtil.fail(AUTH_CAPTCHA_UNMATCH, "验证码已过期");
//        }

        // 查询平台账户
        LitemallPlatformAccount platformAccount = litemallPlatformAccountService.getById(accountId);

        // 查询商城积分账户
        LitemallCoinAccount coinAccount = coinAccountService.getCoinAccountByUserId(userId);

        // 未查询到平台账户
        if (platformAccount == null) {
            return ResponseUtil.fail(755, "未找到平台账户，请重试");
        }

        // 验证平台账户可用金额
        // todo 确认这种准换是否可行
        Integer targetAmount = Double.valueOf(Double.parseDouble(amount) * 10000).intValue();
        if (targetAmount > platformAccount.getAvailableAmount()) {
            return ResponseUtil.fail(756, "兑换金额不能大于可用金额");
        }

        // todo 使用事务

        // 更新平台账户，减可用金额
        platformAccount.setAvailableAmount(platformAccount.getAvailableAmount() - targetAmount);
        // 增加已用金额
        platformAccount.setUsedAmuont(platformAccount.getUsedAmuont() + targetAmount);

        litemallPlatformAccountService.updatePlatformAccount(platformAccount);

        // 新增平台账户积分交易记录
        LitemallPlatformTransactions transaction = new LitemallPlatformTransactions();
        transaction.setPlatformId(platformAccount.getPlatfromId());
        transaction.setAccountId(platformAccount.getId());
        transaction.setAmount(targetAmount);
        transaction.setType(1);
        transaction.setUserId(userId);
        Integer transId = transactionsService.createTransaction(transaction);


        // 新增商城用户积分交易记录
        LitemallCoinRecords coinRecord = new LitemallCoinRecords();
        coinRecord.setUserId(userId);
        coinRecord.setType(0);
        coinRecord.setAmount(targetAmount);
        coinRecord.setTransId(transId);
        coinRecordsService.createCoinRecord(coinRecord);


        // 更新用户积分
        // 未查询到商城积分账户
        if (coinAccount == null) {
            // 创建空白积分账户，默认有效期三年
            // todo 有效期的业务逻辑需要再次确认，把有效期的设置放到配置当中
            LitemallCoinAccount litemallCoinAccount = new LitemallCoinAccount();
            litemallCoinAccount.setUserId(userId);
            litemallCoinAccount.setAvailableAmount(targetAmount);
            litemallCoinAccount.setTotalAmount(targetAmount);
            litemallCoinAccount.setUsedAmount(0);
            litemallCoinAccount.setUnavailableAmount(0);
            litemallCoinAccount.setStartTime(LocalDateTime.now());
            litemallCoinAccount.setEndTime(LocalDateTime.now().plusYears(3));
            coinAccountService.createCoinAccount(litemallCoinAccount);
        } else {
            coinAccount.setAvailableAmount(coinAccount.getAvailableAmount() + targetAmount);
            coinAccount.setTotalAmount(coinAccount.getTotalAmount() + targetAmount);
            coinAccountService.updateCoinAccount(coinAccount);
        }

        return ResponseUtil.ok();
    }

    private int createBlankCoinAccountByUserId(Integer userId) {
        // 创建空白积分账户，默认有效期三年
        // todo 有效期的业务逻辑需要再次确认，把有效期的设置放到配置当中
        LitemallCoinAccount coinAccount = new LitemallCoinAccount();
        coinAccount.setUserId(userId);
        coinAccount.setAvailableAmount(0);
        coinAccount.setUsedAmount(0);
        coinAccount.setTotalAmount(0);
        coinAccount.setUnavailableAmount(0);
        coinAccount.setStartTime(LocalDateTime.now());
        coinAccount.setEndTime(LocalDateTime.now().plusYears(3));
        return coinAccountService.createCoinAccount(coinAccount);
    }

}
