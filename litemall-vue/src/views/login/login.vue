<template>
	<div class="login">
    	<div class="store_header">
		<div class="store_avatar">
			<img src="../../assets/images/avatar_default.png" alt="头像" width="55" height="55">
		</div>
		<div class="store_name">litemall-vue</div>
	</div>

    <md-field-group>
      <md-field
        v-model="mobile"
        icon="mobile"
        placeholder="请输入手机号"
        right-icon="clear-full"
        name="user"
        type="number"
        data-vv-as="帐号"
        @right-click="clearText"
      />

      <div class="captcha-wrapper">
        <md-field
          v-model="captcha"
          icon="lock"
          max-length="4"
          placeholder="图形验证码"
          data-vv-as="图形验证码"
          name="captcha">
        </md-field>

        <div class="captcha-img-wrapper">
          <img :src="codeImg" @click="getCaptcha">
        </div>
      </div>



      <md-field v-model="code" icon="lock" placeholder="请输入验证码">
        <div slot="rightIcon" @click="getCode" class="getCode red">
          <countdown v-if="counting" :time="60000" @end="countDownEnd">
            <template slot-scope="props">{{ +props.seconds || 60 }}秒后获取</template>
          </countdown>
          <span v-else>获取验证码</span>
        </div>
      </md-field>

      <!-- todo 用户协议-->


      <van-button size="large" type="danger" :loading="isLogining" @click="loginSubmit">登录</van-button>
    </md-field-group>


      <div class="text-desc text-center bottom_positon">技术支持: litemall</div>

	</div>
</template>

<script>
import field from '@/components/field/';
import fieldGroup from '@/components/field-group/';

import { authLoginByAccount, authRegisterCaptcha, getKaptcha } from '@/api/api';
import { setLocalStorage } from '@/utils/local-storage';
import { emailReg, mobileReg } from '@/utils/validate';

import { Toast } from 'vant';


export default {
  name: 'login-request',
  components: {
    [field.name]: field,
    [fieldGroup.name]: fieldGroup,
    Toast
  },
  data() {
    return {
			mobile: '',
			captcha: '',
			codeImg: '',
      code: '',
			counting: false,
      isLogining: false,
      userInfo: {}
    };
  },

  created() {
  	this.getCaptcha()
  },

  methods: {
    clearText() {
      this.mobile = '';
    },

    validate() {

    },

    login() {
      let loginData = {
      	mobile: this.mobile,
        code: this.code
      };
      authLoginByAccount(loginData).then(res => {

      	if (res.data.errno === 0) {
					this.userInfo = res.data.data.userInfo;
					setLocalStorage({
						Authorization: res.data.data.token,
						avatar: this.userInfo.avatarUrl,
						nickName: this.userInfo.nickName,
						mobile: this.userInfo.mobile
					});

					this.routerRedirect();
        } else {
      		Toast.fail(res.data.errmsg)
        }

      }).catch(error => {
        Toast.fail(error.data.errmsg);
      });
    },

    loginSubmit() {

			// 先判断手机号
			if (!this.mobile) {
				Toast.fail('请输入手机号')
				return false
			}

			if (!/^1\d{10}$/.test(this.mobile)) {
				Toast.fail('请输入正确的手机号')
				return false
			}

			// 验证是否输入短信验证码
      if (!this.code) {
      	Toast.fail('请输入短信验证码')
        return false
      }

      if (!/^\d{4,6}$/.test(this.code)) {
      	Toast.fail('请输入正确的短信验证码')
        return false
      }

      this.isLogining = true;
      try {
        this.validate();
        this.login();
        this.isLogining = false;
      } catch (err) {
        console.log(err.message);
        this.isLogining = false;
      }
    },

    routerRedirect() {
      // const { query } = this.$route;
      // this.$router.replace({
      //   name: query.redirect || 'home',
      //   query: query
      // });
      window.location = '#/user/';
    },

		getCode() {

    	if (this.counting)  {
    		return false
      }

    	// 先判断手机号
      if (!this.mobile) {
      	Toast.fail('请输入手机号')
				return false
      }

      if (!/^1\d{10}$/.test(this.mobile)) {
      	Toast.fail('请输入正确的手机号')
				return false
      }


    	// 判断图形验证码

      if (!this.captcha) {
				Toast.fail('请输入图形验证码')
      	return false
      }

      if (!/^[0-9a-zA-Z]{4}$/.test(this.captcha)) {
      	Toast.fail('请输入正确的图形验证码')
        return false
      }

			this.counting = true;
			let data = {
				mobile: this.mobile,
        captcha: this.captcha
			};
			authRegisterCaptcha(data).then(res => {
				this.counting = true;
			}).catch(error => {
				Toast.fail(error.data.errmsg);
				if (error.data.errno  === 751) {
					this.getCaptcha()
        }

				if (error.data.errno === 751 || error.data.errno === 752) {
					this.countDownEnd()
        } else {
					this.counting = true;
				}
			});
		},

		getCaptcha() {
			getKaptcha().then(response => {
				this.codeImg = response.data.data
			})
    },

		getLoginData() {
      const password = this.password;
      const account = this.getUserType(this.account);
      return {
        [account]: this.account,
        password: password
      };
    },

    getUserType(account) {
      const accountType = mobileReg.test(account)
        ? 'mobile'
        : emailReg.test(account)
        ? 'email'
        : 'username';
      return accountType;
    },

		countDownEnd() {
			this.counting = false;
		}
  }
};
</script>


<style lang="scss" scoped>
@import '../../assets/scss/mixin';
.login {
  position: relative;
  background-color: #fff;
}
.store_header {
  text-align: center;
  padding: 30px 0;
  .store_avatar img {
    border-radius: 50%;
  }
  .store_name {
    padding-top: 5px;
    font-size: 16px;
  }
}
.captcha-wrapper {
  display: flex;
  align-items: center;
  justify-content: flex-start;

  .captcha-img-wrapper {
    padding-left: 5px;
    display: flex;
    align-items: center;
    justify-content: center;

    img {
      border-radius: 5px;
    }
  }

}
.register {
  padding-top: 40px;
  color: $font-color-gray;
  a {
    color: $font-color-gray;
  }
  > div {
    width: 50%;
    box-sizing: border-box;
    padding: 0 20px;
  }
  .connect {
    @include one-border(right);
    text-align: right;
  }
}
.bottom_positon {
  position: absolute;
  bottom: 30px;
  width: 100%;
}
</style>
