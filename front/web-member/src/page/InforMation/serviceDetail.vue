<template>
  <div>
      <top :address="false" active="1"  ref="top"/>
      <head-nav :active="5"></head-nav>
      <detail :links="links" isIndex></detail>
  </div>
</template>

<script>
import detail from '~page/personGate/components/serviceDetail'
import top from '~src/top'
import headNav from '~page/51index/components/nav'
export default {
  components: {
    detail,
    top,
    headNav
  },
  data () {
    return {
      index: 8,
      links:['/51index/serviceList/all','/51index/serviceList']
    }
  },
  created() {
    // 0 垂钓 1采摘 2景区 3餐饮 4住宿
  },
  methods: {
  }
}
</script>

<style lang="scss" scoped>

</style>
