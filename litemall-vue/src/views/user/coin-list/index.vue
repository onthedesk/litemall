<template>
<div>
  <div class="list">
    <div class="th">
      <div class="td">订单号</div>
      <div class="td">交易类型</div>
      <div class="td">金额金额</div>
    </div>
    <van-pull-refresh v-model="isRefreshing" @refresh="onRefresh">
      <van-list class="device-list" v-model="isLoading" :finished="isFinished" finished-text="没有更多了" @load="onListLoad">
        <div class="item" v-for="(item,index) in list" :key="index">
          <div class="top">
            <div class="name">{{ item.transId }}</div>
            <div class="type">{{ item.type | typeFilter }}</div>
            <div class="amount">{{ Number(item.amount / 10000 ).toFixed(2) }}</div>
          </div>
          <div class="time">{{item.addTime}}</div>
        </div>
      </van-list>
    </van-pull-refresh>
  </div>
</div>
</template>

<script>
  import { userCoinRecords } from '@/api/api'
	import Vue from 'vue'
	import { PullRefresh, List } from 'vant'

	Vue.use(PullRefresh)
	Vue.use(List)

	export default {
		name: "coin-list",
		data () {
			return {
				list: [],
				isRefreshing: false,
				isLoading: false,
				isError: false,
				isFinished: true,
				currentPage: 0
			}
		},
    filters: {
			typeFilter(value) {
				let typeStrs = [
					'兑换',
          '订单支付',
          '退款'
        ]
        return typeStrs[value]
      }
    },
    created() {
      userCoinRecords().then(res => {
        this.list = res.data.data.list
      })
		},
    methods: {
			onRefresh() {},
      onListLoad() {}
    }
	}
</script>

<style scoped lang="scss">
.list {
  background: #fff;
  border-radius: 4px;
  padding: 0 15px 10px;
  .th {
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 10px;
    border-bottom: 1px solid #e6e6e6; /*px*/
    .td {
      font-size: 12px;
      line-height: 14px;
      color: #999999;
    }
  }
  .item {
    padding: 10px 0;
    .top {
      display: flex;
      align-items: center;
      justify-content: space-between;
      .name,
      .amount {
        font-size: 14px;
        color: #333333;
        line-height: 16px;
      }
    }
    .time {
      text-align: right;
      margin-top: 8px;
      font-size: 14px;
      line-height: 16px;
      color: #999999;
    }
  }
}
</style>
