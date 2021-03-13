<template>
  <div class="user_header">
    <van-icon name="set" class="user_set" @click="toSetting"/>
    <!--<div class="user_avatar">-->
      <!--<img :src="avatar" alt="头像" width="55" height="55">-->
    <!--</div>-->
    <div class="nickname_wrapper">Hi, {{nickName}}</div>

    <div class="coin_wrapper" @click="toCoin">
      <div class="coin_box" :style="{backgroundImage: `url(${background_image})`}">
        <div class="coin_box_title_wrapper">
          我的积分
        </div>
        <div class="coin_box_content_wrapper">
          <div class="coin_box_amount">
            <span class="number">{{amount}}</span>
            <span class="unit">积分</span>
          </div>
          <div class="coin_box_due_date" v-if="dueDate">
            有效期至{{dueDate}}
          </div>
        </div>
      </div>

      <div class="right-arrow">
        <van-icon name="arrow"></van-icon>
      </div>
    </div>
  </div>
</template>

<script>
import avatar_default from '@/assets/images/avatar_default.png';
import bg_default from '@/assets/images/user_head_bg.png';
import { getLocalStorage } from '@/utils/local-storage';
import { userCoin } from '@/api/api'

export default {
  name: 'user-header',

  props: {
    isLogin: {
      type: Boolean,
      default: false
    }
  },

  data() {
    return {
      nickName: '',
      avatar: avatar_default,
      background_image: bg_default,
      amount: 0,
			dueDate: ''
    };
  },

  created() {
		this.getUserInfo();
		userCoin().then(res => {
      if (res.data.data.coin) {
      	let coin = res.data.data.coin
      	this.amount = Number(coin.availableAmount / 10000).toFixed(2)
        this.dueDate = coin.endTime.split(' ')[0]
      }
    }).catch(error => {
    	Toast.fail(error.data.errmsg)
    })
  },

  methods: {
    getUserInfo() {
      const infoData = getLocalStorage(
        'nickName',
        'avatar',
        'mobile'
      );
      this.avatar = infoData.avatar || avatar_default;
      this.nickName = infoData.nickName || infoData.mobile.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
    },
    toSetting() {
      this.$router.push({ name: 'user-information' });
    },
		toCoin() {
    	this.$router.push({
        name: 'user-coin-list'
      })
    }
  }
};
</script>

<style lang="scss" scoped>
.user_header {
  background-repeat: no-repeat;
  background-size: cover;
  text-align: center;
  color: #333;
  padding-top: 30px;
  background-color: #fff;

  .nickname_wrapper {
    font-size: 18px;
    text-align: left;
    padding-left: 20px;
  }

  .coin_wrapper {
    position: relative;
    .coin_box {
      margin: 20px 10px 10px;
      padding: 15px 15px 10px;
      color: #fff;
      text-align: left;
      border-radius: 5px;

      .coin_box_title_wrapper {
        font-size: 12px;
        margin-bottom: 5px;
      }

      .coin_box_content_wrapper {
        display: flex;
        justify-content: space-between;
        align-items: baseline;

        .coin_box_amount {

          display: flex;
          align-items: baseline;

          .number {
            font-size: 22px;
            font-weight: 500;
            margin-right: 5px;
          }

          .unit {
            font-weight: 400;
            margin-right: 5px;
          }
        }

        .coin_box_due_date {
          font-size: 12px;
          font-weight: 300;
          margin-right: 20px;
        }
      }
    }

    .right-arrow {
      position: absolute;
      color: #fff;
      top: 40%;
      right: 20px;
      .van-icon {
        font-size: 12px;
      }
    }
  }
}

.user_set {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 24px;
}
.user_avatar {
  margin-bottom: 10px;
  img {
    border: 0;
    border-radius: 50%;
  }
}
</style>
