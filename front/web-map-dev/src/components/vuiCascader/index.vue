<template>
  <Cascader
    class="vui-cascader"
    :class="{vuiCascaderActive: show}"
    :data="data"
    @on-visible-change="handleToggle"
    :change-on-select="changeOnSelect"
    :render-format="format"
    :load-data="loadData"
    :disabled="disabled"
    @on-change="handleGetData"
    :size="size =='default' ? 'small' : size"
    >
      <Input v-if="value" v-model="value" :icon="icon" readonly :size="size" :disabled="disabled"/>
      <Input v-else v-model="values" :icon="icon" readonly :size="size" :disabled="disabled"/>
  </Cascader>
</template>
<script>
export default {
  props: {
    values: String,
    isCheckedCity: {
      type: Boolean,
      default: false
    },
    isCheckedCounty: {
      type: Boolean,
      default: false
    },
    disabled: {
      type: Boolean,
      default: false
    },
    changeOnSelect: {
      type: Boolean,
      default: true
    },
    size: {
      type: String,
      default: 'default'
    }
  },
  data: () => ({
    data: [],
    icon: 'md-arrow-dropdown',
    value: '',
    selectedData: '',
    show: false
  }),
  created () {
    this.value = this.values
    this.$api.post('/member/town/next/4cc0ce9b1b8d1e8ab8c005056bc3816').then(res => {
      this.data = res.data
    })
  },
  watch: {
    values: {
      handler (curVal, oldVal) {
        console.log('curVal', curVal, 'oldVal', oldVal)
        this.value = curVal
      },
      deep: true
    }
  },
  methods: {
    format (labels, selectedData) {
      this.value = labels.join('/')
    },
    loadData (item, callback) {
      item.loading = true
      this.$api.post(`/member/town/next/${item.value}`).then(res => {
        item.loading = false
        let data = []
        // isCheckedCity 作为判断是否是只需要选择到省市的标识，
        // this.selectedData在on-change的时候改变，该判断执行之前，数据还未变更，所以要判断大于等于1
        if (this.isCheckedCity && this.selectedData.length >= 1) {
          res.data.forEach(e => {
            let obj = {}
            obj.label = e.label
            obj.value = e.value
            data.push(obj)
          })
        } else if (this.isCheckedCounty && item.__label.split(' / ').length >= 3) {
          // isCheckedCounty 选中到区县一级
          res.data.forEach(e => {
            let obj = {}
            obj.label = e.label
            obj.value = e.value
            data.push(obj)
          })
        } else {
          data = res.data
        }
        item.children = data
        callback()
      })
    },
    handleToggle (flag) {
      if (flag) {
        this.show = true
      } else {
        this.show = false
      }
    },
    handleGetData (value, selectedData) {
      this.$emit('handle-get-result', value, selectedData)
      this.selectedData = selectedData
    }
  }
}
</script>
<style lang="less">
.vui-cascader {
  &.vuiCascaderActive{
    .ivu-input-icon{
      transition: all .2s ease-in-out;
      transform: rotate(180deg);
    }
  }
}
</style>
