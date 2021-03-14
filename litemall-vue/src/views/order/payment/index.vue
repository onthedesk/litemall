<template>
  <div class="payment">
    <div class="time_down payment_group">
      请在
      <span class="red">半小时内</span>
      完成付款，否则系统自动取消订单
    </div>

    <van-cell-group class="payment_group">
      <van-cell title="订单编号" :value="order.orderInfo.orderSn"/>
      <van-cell title="实付金额">
        <span class="red">{{order.orderInfo.actualPrice * 100 | yuan}}</span>
      </van-cell>
    </van-cell-group>

    <div class="pay_way_group">
      <div class="pay_way_title">选择支付方式</div>
      <van-radio-group v-model="payWay">
        <van-cell-group>
          <van-cell class="pay_type_wrapper" title-class="pay_type_title">
            <template slot="title">
              <van-icon name="gift" class="pay_type_icon coin"></van-icon>
              <span class="pay_type_text">积分支付</span>
              <span class="balance">(余额: {{ this.coinAmount }})</span>
            </template>
            <template slot="right-icon">
              <van-radio name="coin"/>
            </template>
          </van-cell>
          <van-cell class="pay_type_wrapper" title-class="pay_type_title">
            <template slot="title">
              <van-icon name="alipay" class="pay_type_icon alipay"></van-icon>
              <span class="pay_type_text">支付宝</span>
            </template>
            <template slot="right-icon">
              <van-radio name="ali"/>
            </template>
          </van-cell>
          <van-cell class="pay_type_wrapper" title-class="pay_type_title">
            <template slot="title">
              <van-icon name="wechat-pay" class="pay_type_icon wechat"></van-icon>
              <span class="pay_type_text">微信支付</span>
            </template>
            <template slot="right-icon">
              <van-radio name="wx"/>
            </template>
          </van-cell>
        </van-cell-group>
      </van-radio-group>
    </div>

    <van-button class="pay_submit" @click="pay" type="primary" bottomAction>去支付</van-button>
  </div>
</template>

<script>
import { Radio, RadioGroup, Dialog, Toast} from 'vant';
import { orderDetail, orderPrepay, orderH5pay, userCoin, coinPay } from '@/api/api';
import _ from 'lodash';
import { getLocalStorage, setLocalStorage } from '@/utils/local-storage';

export default {
  name: 'payment',

  data() {
    return {
      payWay: 'coin',
      order: {
        orderInfo: {},
        orderGoods: []
      },
			payWayStr: {
				wx: '微信支付',
				coin: '积分支付',
				alipay: '支付宝支付'
			},
			coinAmount: 0,
      orderId: 0
    };
  },
  created() {
    if (_.has(this.$route.params, 'orderId')) {
      this.orderId = this.$route.params.orderId;
      this.getOrder(this.orderId);
    }

		userCoin().then(res => {
			if (res.data.data.coin) {
				let coin = res.data.data.coin
				this.coinAmount = Number(coin.availableAmount / 10000).toFixed(2)
			}
		}).catch(error => {
			Toast.fail(error.data.errmsg)
		})
  },
  methods: {
    getOrder(orderId) {
      orderDetail({orderId: orderId}).then(res => {
        this.order = res.data.data;
      });
    },
    pay() {

			if (this.payWay === 'coin' && (this.order.orderInfo.actualPrice > this.coinAmount)) {
				Toast.fail('积分余额不足，请选择其他支付方式')
        return false
      }

      Dialog.alert({
        message: '您选择了' + this.payWayStr[this.payWay]
      }).then(() => {
        if (this.payWay === 'wx') {
          let ua = navigator.userAgent.toLowerCase();
          let isWeixin = ua.indexOf('micromessenger') != -1;
          if (isWeixin) {
            orderPrepay({ orderId: this.orderId })
              .then(res => {
                let data = res.data.data;
                let prepay_data = JSON.stringify({
                  appId: data.appId,
                  timeStamp: data.timeStamp,
                  nonceStr: data.nonceStr,
                  package: data.packageValue,
                  signType: 'MD5',
                  paySign: data.paySign
                });
                setLocalStorage({ prepay_data: prepay_data });

                if (typeof WeixinJSBridge == 'undefined') {
                  if (document.addEventListener) {
                    document.addEventListener(
                      'WeixinJSBridgeReady',
                      this.onBridgeReady,
                      false
                    );
                  } else if (document.attachEvent) {
                    document.attachEvent(
                      'WeixinJSBridgeReady',
                      this.onBridgeReady
                    );
                    document.attachEvent(
                      'onWeixinJSBridgeReady',
                      this.onBridgeReady
                    );
                  }
                } else {
                  this.onBridgeReady();
                }
              })
              .catch(err => {
                Dialog.alert({ message: err.data.errmsg });
                that.$router.replace({
                  name: 'paymentStatus',
                  params: {
                    status: 'failed'
                  }
                });
              });
          } else {
            orderH5pay({ orderId: this.orderId })
              .then(res => {
                let data = res.data.data;
                window.location.replace(
                  data.mwebUrl +
                  '&redirect_url=' +
                  encodeURIComponent(
                    window.location.origin +
                    '/#/?orderId=' +
                    this.orderId +
                    '&tip=yes'
                  )
                );
              })
              .catch(err => {
                Dialog.alert({ message: err.data.errmsg });
              });
          }
        } else if (this.payWay === 'coin') {
            // 积分支付
					coinPay({
            orderId: this.orderId
          }).then( res => {
          	if (res.data.errno === 0) {
          		Toast.success("恭喜您，订单支付成功")
          		this.$router.push('/user')
            }
          }).catch(error => {
          	Toast.fail(error.data.errmsg)
          })


        } else {
					//todo : alipay
        }
      });
    },
    onBridgeReady() {
      let that = this;
      let data = getLocalStorage('prepay_data');
      // eslint-disable-next-line no-undef
      WeixinJSBridge.invoke(
        'getBrandWCPayRequest',
        JSON.parse(data.prepay_data),
        function(res) {
          if (res.err_msg == 'get_brand_wcpay_request:ok') {
            that.$router.replace({
              name: 'paymentStatus',
              params: {
                status: 'success'
              }
            });
          } else if (res.err_msg == 'get_brand_wcpay_request:cancel') {
            that.$router.replace({
              name: 'paymentStatus',
              params: {
                status: 'cancel'
              }
            });
          } else {
            that.$router.replace({
              name: 'paymentStatus',
              params: {
                status: 'failed'
              }
            });
          }
        }
      );
    }
  },

  components: {
    [Radio.name]: Radio,
    [RadioGroup.name]: RadioGroup,
    [Dialog.name]: Dialog
  }
};
</script>

<style lang="scss" scoped>
.payment_group {
  margin-bottom: 10px;
}

.time_down {
  background-color: #fffeec;
  padding: 10px 15px;
}

.pay_type_title {
  display: flex;
}

.pay_type_icon {
  font-size: 24px;
  line-height: 32px;

  &.coin {
    color: #db3d3c;
  }

  &.alipay {
    color: #027aff;
  }

  &.wechat {
    color: #07c160;
  }
}

.pay_type_text {
  display: inline-block;
  line-height: 32px;
  padding-left: 5px;
}

.balance {
  display: inline-block;
  line-height: 32px;
  padding-left: 5px;
  color: #027aff;
}

.pay_submit {
  position: fixed;
  bottom: 0;
  width: 100%;
}

.pay_way_group img {
  vertical-align: middle;
}

.pay_way_title {
  padding: 15px;
  background-color: #fff;
}
</style>
