<template>
  <vui-wrapper class="new-auth">
    <vui-tab
    :id="tabId"
    slot="tab"
    :title="tabTitle"
    :data="tabData"
    @on-click="onTabClick"
    @handleEdit="handleEdit"
    :appId="appId"
    class="mr15"
    style="width:200px"></vui-tab>
    <component slot="content" v-bind:is="mode" :id="modeId" :appId="appId" :yearId="yearId" @on-save="onSave" :ref="mode"></component>
  </vui-wrapper>
</template>

<script>
import vuiWrapper from '../components/wrapper'
import vuiTab from '../components/tab'
import landUseAuthority from './landUseAuthority'
import houseUseRights from './houseUseRights'
import equipmen from './equipment'
import transportation from './transportation'
import householdAssets from './householdAssets'
import facilityAgriculture from './facilityAgriculture'
export default {
  components: {
    vuiWrapper,
    vuiTab,
    landUseAuthority,
    houseUseRights,
    equipmen,
    transportation,
    householdAssets,
    facilityAgriculture
  },
  props: {
    yearId: {
      type: String
    },
    appId: {
      type: String
    }
  },
  data() {
    return {
      activeInidex: 0,
      tabTitle: '资产设置',
      tabData: [
      //   {
      //   title: '土地使用权限信息',
      //   id: '1',
      //   name: 'landUseAuthority',
      //   checked: true,
      //   status: false
      // },{
      //   title: '房屋使用权信息',
      //   id: '1',
      //   name: 'houseUseRights',
      //   checked: false,
      //   status: false
      // },{
      //   title: '生产类机械设备信息',
      //   id: '1',
      //   name: 'equipmen',
      //   checked: false,
      //   status: false
      // },{
      //   title: '交通运输类工具信息',
      //   id: '1',
      //   name: 'transportation',
      //   checked: false,
      //   status: false
      // },{
      //   title: '家用资产信息',
      //   id: '1',
      //   name: 'householdAssets',
      //   checked: false,
      //   status: false
      // },{
      //   title: '设施农业信息',
      //   id: '1',
      //   name: 'facilityAgriculture',
      //   checked: false,
      //   status: false
      // }
      ],
      mode: 'landUseAuthority',
      modeId: '',
      tabId: '123'
    }
  },
  created() {
    this.account = this.$user.loginAccount
    this.handleInit()
  },
  methods: {
    handleInit () {
      this.$api.post('/member-reversion/perfect/initData',{
        account: this.$user.loginAccount,
        templateId: this.$template.id,
        yearId: this.yearId,
        appId: this.appId
      }).then(response => {
        if (response.code === 200) {
          this.tabData = []
          response.data.subModule.forEach((element, index) => {
            this.tabData.push({
              title: element.name,
              name: element.url,
              id: element.dictId,
              checked: index === this.activeInidex ? true : false,
              status: element.isComplete
            })
          })
          this.onTabClick(this.tabData[this.activeInidex].name, this.tabData[this.activeInidex], this.activeInidex)
          console.log('调用点击事件')
          this.tabTitle = response.data.moduleName
        }
      })
    },
    // 修改model
    handleEdit () {
      this.$emit('handleRefresh')
      this.handleInit()
    },
     // 选中的标签
    onTabClick (name, data, index) {
      this.mode = data.name
      this.modeId = data.id
      this.activeInidex = index
      console.log(this.mode)
      this.$nextTick(e => {
        this.$refs[this.mode].handleInit()
      })
    },
    // 保存切换状态需要判断表单是否完成
    onSave (){
      this.tabData.forEach(item => {
        if(item.name === this.mode) item.status = true
      })
    }
  }
}
</script>

<style lang="scss">

</style>
