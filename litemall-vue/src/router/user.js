const tab_user = () => import('@/views/user/tabbar-user');
const UserCollect = () => import('@/views/user/module-collect');
const UserAddress = () => import('@/views/user/module-address');
const UserAddressEdit = () => import('@/views/user/module-address-edit');
const UserServer = () => import('@/views/user/module-server');
const UserHelp = () => import('@/views/user/module-help');
const UserFeedback = () => import('@/views/user/module-feedback');

const UserInformation = () => import('@/views/user/user-information-set');
const UserInfo_SetMobile = () => import('@/views/user/user-information-set/set-mobile');
const UserInfo_SetNickname = () => import('@/views/user/user-information-set/set-nickname');
const UserInfo_SetPassword = () => import('@/views/user/user-information-set/set-password');

const UserOrderList = () => import('@/views/user/order-list');
const UserCouponList = () => import('@/views/user/coupon-list');
const UserRefundList = () => import('@/views/user/refund-list');
const UserCoinList = () => import('@/views/user/coin-list')
const UserCoinExchange = () => import('@/views/user/coin-exchange')

const Tabbar = () => import('@/components/Tabbar/');

export default [
  {
    path: '/user',
    name: 'user',
    meta: {
      keepAlive: false,
      login: true,
      showHeader:false
    },
    components: { default: tab_user, tabbar: Tabbar }
  },
  {
    path: '/user/collect',
    name: 'collect',
    meta: {
      login: true
    },
    component: UserCollect
  },
  {
    path: '/user/address',
    name: 'address',
    meta: {
      login: true
    },
    component: UserAddress
  },
  {
    path: '/user/address/edit',
    name: 'address-edit',
    props: true,
    meta: {
      login: true,
      showHeader:false
    },
    component: UserAddressEdit
  },
  {
    path: '/user/server',
    name: 'user-server',
    component: UserServer
  },
  {
    path: '/user/help',
    name: 'user-help',
    component: UserHelp
  },
  {
    path: '/user/feedback',
    name: 'user-feedback',
    component: UserFeedback
  },
  {
    path: '/user/information',
    name: 'user-information',
    meta: {
      login: true
    },
    component: UserInformation
  },
  {
    path: '/user/information/setMobile',
    name: 'user-info-setMobile',
    component: UserInfo_SetMobile
  },
  {
    path: '/user/information/setNickname',
    name: 'user-info-setNickname',
    component: UserInfo_SetNickname
  },
  {
    path: '/user/information/setPassword',
    name: 'user-info-setPassword',
    component: UserInfo_SetPassword
  },
  {
    path: '/user/order/list/:active',
    name: 'user-order-list',
    props: true,
    meta: {
      title: '我的订单',
      showHeader: true
    },
    component: UserOrderList
  },
  {
    path: '/user/coupon/list/:active',
    name: 'user-coupon-list',
    props: true,
    meta: {
      title: '我的优惠券',
      showHeader: true
    },
    component: UserCouponList
  },
  {
    path: '/user/refund/list',
    name: 'user-refund-list',
    component: UserRefundList
  },
  {
    path: '/user/coin/list',
    name: 'user-coin-list',
    meta: {
      title: '我的积分',
      showHeader: true
    },
    component: UserCoinList
  },
  {
    path: '/user/coin/exchange',
    name: 'user-coin-exchange',
    meta: {
      title: '积分兑换',
      showHeader: true
    },
    component: UserCoinExchange
  }
];
