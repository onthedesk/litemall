<template>
<div>
    <van-nav-bar :title="titleHeader" left-text="返回" left-arrow @click-left="goBack" v-show="showHeader"/>
</div>

</template>
<script>
import { NavBar } from 'vant';
import { mapGetters } from 'vuex';

export default {
    name:"v-header",
    computed: {
        ...mapGetters(['titleHeader']),
        showHeader:function(){
            let header=this.$store.getters.showHeader;
            return header
        }
    },
    methods: {
        goBack() {
            this.$router.back(-1);
        }
   },
   components: {
    [NavBar.name]:NavBar,
  }
}
</script>
