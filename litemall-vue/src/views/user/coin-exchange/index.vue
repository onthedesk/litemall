<template>
<div class="bind-wrapper">
  <van-steps :active="active">
    <van-step>实名认证</van-step>
    <van-step>查询账户</van-step>
    <van-step>积分兑换</van-step>
    <van-step>交易完成</van-step>
  </van-steps>

  <div class="name-verify-wrapper" v-if="active === 0">
    <van-cell-group title="实名认证">

    <van-form @submit="onNameVerifySubmit">
      <van-field
              v-model="name"
              name="姓名"
              autofocus
              placeholder="请输入姓名"
              label="姓名">
      </van-field>
      <van-field
              v-model="idCard"
              type="text"
              name="身份证号"
              placeholder="请输入身份证号"
              label="身份证号">
      </van-field>
      <div style="margin: 16px;">
        <van-button round block type="danger" native-type="submit">下一步</van-button>
      </div>
    </van-form>
    </van-cell-group>
  </div>

  <div class="platform-list-wrapper" v-if="active === 1">
    <div class="item" v-for="(item, index) in list" :key="index">
      <div class="item-wrapper">
        <div class="number-warp">
          <span class="no">平台名称: {{ item.platformName }} </span>
          <span class="state">状态</span>
        </div>
        <div class="view">查看合同</div>
      </div>
      <div class="item-wrapper">
        <div class="name">注册手机号</div>
        <div class="value">{{ item.mobile }}</div>
      </div>
      <div class="item-wrapper">
        <div class="name">债权余额</div>
        <div class="value amount">{{ item.totalAmount }}</div>
      </div>
      <div class="item-wrapper">
        <div class="name">净债权余额</div>
        <div class="value amount">{{ item.dueAmount }}</div>
      </div>
      <div class="item-wrapper">
        <div class="name">已兑换余额</div>
        <div class="value amount">{{ item.usedAmount }}</div>
      </div>
      <div class="item-wrapper">
        <div class="name">可兑换余额</div>
        <div class="value amount">{{ item.availableAmount }}</div>
      </div>
      <div class="panel-wrapper">
        <div class="btn cancel">联系客服</div>
        <div class="btn continue" @click="toExchange(item)">立即兑换</div>
      </div>
    </div>
  </div>

  <div class="mobile-verify-wrapper" v-if="active === 2">
    <van-cell-group title="积分兑换">

      <van-form @submit="onExchangeSubmit">
        <van-field
          v-model="currentPlatform.platformName"
          name="平台名称"
          readonly
          label="平台名称">
        </van-field>
        <van-field
          v-model="currentPlatform.mobile"
          name="手机号"
          readonly
          label="手机号">
        </van-field>
        <van-field
          v-model="currentPlatform.availableAmount"
          name="可兑换金额"
          readonly
          label="可兑换金额">
        </van-field>
        <van-field
          v-model="amount"
          type="number"
          name="兑换金额"
          autofocus
          placeholder="请输入兑换金额"
          label="兑换金额">
        </van-field>
        <van-field
          v-model="code"
          type="number"
          name="验证码"
          placeholder="请输入验证码"
          label="验证码">
          <template #button>
            <countdown v-if="counting" :time="60000" @end="countDownEnd">
              <template slot-scope="props">{{ +props.seconds || 60 }}秒后获取</template>
            </countdown>
            <van-button
              plain
              v-else
              size="small" type="primary" @click="getCode">获取验证码</van-button>
          </template>
        </van-field>
        <div class="notify-failed-wrapper">
          <span>收不到验证码？</span>
        </div>
        <div style="margin: 16px;">
          <van-button round block type="danger" native-type="submit">立即兑换</van-button>
        </div>
      </van-form>
    </van-cell-group>
  </div>

</div>
</template>

<script>

  import { coinAccount, authCaptcha } from '@/api/api'

  import { Form, Field, Step, Steps, Toast, Dialog } from 'vant'
  import Vue from 'vue'
  Vue.use(Form)
  Vue.use(Field)
  Vue.use(Step)
  Vue.use(Steps)
	export default {
		name: "index",
    data() {
			return {
				name: '',
        idCard: '',
        list: [],
        code: '',
        amount: '',
				counting: false,
				active: 0,
        currentPlatform: null
      }
    },
    created() {
      this.getAccount()
    },
    methods: {
			onNameVerifySubmit() {

				if (this.name === '') {
					Toast.fail('请输入姓名')
          return false
        }

				if (!/^[\u4E00-\u9FA5]{2,4}$/.test(this.name)) {
					Toast.fail('请输入正确的姓名')
          return false
        }

				if (this.idCard === '') {
					Toast.fail('请输入身份证号')
          return false
        }

				if (!/^([1-6][1-9]|50)\d{4}(18|19|20)\d{2}((0[1-9])|10|11|12)(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/.test(this.idCard)) {
					Toast.fail('请输入正确的身份证号')
          return false
        }

				Dialog.confirm({
					title: '请确认',
					message: '认证成功后，将为您绑定名下债权账户。',
				}).then(() => {
						// on confirm
            coinAccount({
              name: this.name,
              idCard: this.idCard
            }).then(res => {
              let data = res.data.data
              if (data.accounts && data.accounts.length > 0) {
                this.name = data.accounts[0].realName
                this.idCard = data.accounts[0].idcardNumber
                this.active = 1
              } else {
                Toast.fail(data.errmsg)
              }
            }).catch(error => {
              Toast.fail(error.data.errmsg)
            })

          }).catch(() => {
						// on cancel
					});
      },
      getAccount() {
				coinAccount({}).then(res => {
					let data = res.data.data
					if (data.accounts && data.accounts.length > 0) {
						this.name = data.accounts[0].realName
						this.idCard = data.accounts[0].idcardNumber
            this.list = data.accounts
						this.active = 1
					}  else {
						this.active = 0
					}
				}).catch(error => {
					// 未查询到账户
					Toast.fail(error.data.errmsg)
				})
      },
			onExchangeSubmit() {

      },
			toExchange(item) {
				this.currentPlatform = item
				this.active += 1
      },
      getCode() {

				if (!this.currentPlatform.mobile) {
					Toast.fail('请联系平台客服，更新您的手机号')
          return false
        }

				this.counting = true
				authCaptcha({
          mobile: this.currentPlatform.mobile
        }).then(res => {

        }).catch(error => {
        	Toast.fail(error.data.errmsg)
        })
      },
			countDownEnd() {
				this.counting = false;
			}
    }
	}
</script>

<style scoped lang="scss">

  .platform-list-wrapper {
    padding: 10px 15px;
  }

  .item {
    margin-bottom: 10px;
    background: #fff;
    border-radius: 6px;
    padding: 10px 15px;
    .item-wrapper {
      height: 37px;
      border-bottom: 1px solid #e6e6e6; /*px*/
      display: flex;
      align-items: center;
      justify-content: space-between;
    &:last-child {
       border: none;
     }
    }

  .number-warp {
    display: flex;
    align-items: center;
  .no {
    font-size: 14px;
    color: #333333;
  }
  .state {
    padding: 4px 5px;
    background-color: rgba(44, 174, 139, 0.1);
    border-radius: 2px;
    font-size: 12px;
    line-height: 24px;
    color: #2cae8b;
    text-align: center;
    margin-left: 16px;
    &.status-1, &.status-2, &.status-3, &.status-5, &.status-6 {
        color: #fe662a;
        background-color: rgba(255, 121, 6, 0.1);
      }
    }
  }
  .view {
    font-size: 12px;
    color: #23a6e5;
  }
  .name {
    font-size: 12px;
    color: #999999;
  }
  .value {
    font-size: 14px;
    color: #333333;
  &.amount {
     font-weight: bold;
     color: #fe662a;
   }
  }
  }

  .panel-wrapper {
    margin-top: 10px;
    display: flex;
    justify-content: flex-end;
    .btn {
      width: 78px;
      height: 29px;
      border-radius: 14px;
      text-align: center;
      font-size: 12px;
      font-family: PingFangSC-Regular, PingFang SC;
      font-weight: 400;
      line-height: 29px;
      &.cancel {
        margin-right: 10px;
        border: 1px solid #CCCCCC;
        color: #AAAAAA;
        background: #fff;
      }
      &.continue {
        color: #fff;
        background-color: #db3d3c;
      }
    }
  }

  .notify-failed-wrapper {
    font-size: 12px;
    padding-left: 15px;
    color: #1989fa;
  }

</style>
