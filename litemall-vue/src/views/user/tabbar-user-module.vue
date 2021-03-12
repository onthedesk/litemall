<template>
  <div class="user_module">
    <van-cell-group>
      <van-cell icon="gift-o" title="积分兑换" to="/user/coin/exchange" isLink/>
      <van-cell icon="shoucang" title="我的收藏" to="/user/collect" isLink/>
      <van-cell icon="dingwei" title="收货地址" to="/user/address" isLink/>
      <van-cell icon="kefu" title="服务中心" to="/user/server" isLink/>
    </van-cell-group>
  </div>
</template>

<script>
export default {
  name: 'user-module'
};
</script>


<style scoped lang="scss">
.user_module {
  background-color: #fff;
}
</style>
