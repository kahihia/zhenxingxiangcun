<template>
    <div>
      <Card class="layout pd20">
        <Title title="设置关注"/>
        <vui-follow ref="follow" class="mb40 mt40"></vui-follow>
        <Title title="设置收藏"/>
        <collection class="mb40"></collection>
        <Title title="好友分组"></Title>
        <buddy-group ref="buddyGroup" class="mb40"></buddy-group>
        <Title title="设置账户"></Title>
        <bank-account-setting ref="bankSetting"></bank-account-setting>
        <div class="tc pd20">
          <Button type="primary" @click="handleClickBack" class="back-btn mr20">返回上一步</Button>
          <Button type="primary" @click="handleClickNext">保存并下一步</Button>
        </div>
      </Card>
    </div>
</template>
<script>
import Title from '../components/title'
import vuiFollow from './components/vui-follow'
import buddyGroup from './components/buddy-group'
import bankAccountSetting from './components/bank-account-setting'
import collection from './components/collection'
export default {
  components: {
    vuiFollow,
    buddyGroup,
    bankAccountSetting,
    collection,
    Title
  },
  data: () => ({
    loginUser: JSON.parse(sessionStorage.getItem(sessionStorage.getItem('key'))),
    templateId: '',
    stepId: ''
  }),
  created () {
    console.log('123')
    this.templateId = this.$route.query.templateId
    // 查询模板 步骤
    this.$api.post('/member-reversion/realStep/findStep', {
          account: this.$user.loginAccount,
          templateId: this.templateId
      }).then(response => {
          if (response.code === 200) {
              // reload 存在， 说明是点击模板后查询的 如果 response.data 存在 那么则需要修改url
              if (response.data) {
                  this.stepId = response.data.id
              }
          }
      })
    this.initData()
  },
  methods:{
    initData () {
      this.$api.post('/member-reversion/indivi/findIndividInfo',{
        account: this.loginUser.loginAccount,
        follow_type: 'knowledge',
        templateId: this.templateId
      }).then(response => {
        console.log('init response', response)
        if (response.code === 200) {
          // 回显关注数据
          if (response.data.FollowData[0].follow[0].length === 0 &&
              response.data.FollowData[0].follow[1].length === 0 &&
              response.data.FollowData[0].follow[2].length === 0 &&
              response.data.FollowData[0].releva[0].length === 0 &&
              response.data.FollowData[0].releva[1].length === 0 &&
              response.data.FollowData[0].releva[2].length === 0) {
            this.$refs.follow.listData = []
          } else {
            this.$refs.follow.listData = response.data.FollowData
          }
          // 回显好友分组数据
          this.$refs.buddyGroup.buddyGroupList = response.data.FriendGroupData === '' ? [] : response.data.FriendGroupData
          // 回显银行账户数据
          this.$refs.bankSetting.bankInfo.name = response.data.BankSettingData.name
          this.$refs.bankSetting.bankInfo.id = response.data.BankSettingData.id
          this.$refs.bankSetting.bankInfo.bankCard = response.data.BankSettingData.bankCard
          this.$refs.bankSetting.bankInfo.mobile = response.data.BankSettingData.mobile
        }
      }).catch(error => {
        this.$Message.error('服务器异常！')
      })
    },
    handleClickBack () {
      this.$router.push({
        path: '/auth/step3',
        query: {
          templateId: this.templateId
        }
      })
    },
    handleClickNext () {
      let flag = true
      let obj = this.$refs.bankSetting.bankInfo
      // 调用子组件的表单验证 账户设置这整一块可以不填 只要有填了一个值就触发表单验证
      for (let key in obj) {
        if (obj[key]) {
          flag = this.$refs.bankSetting.handleSubmit()
          break
        }
      }
      if (flag) {
        let data = {
          LoginAccount: this.loginUser.loginAccount,
          FollowData: this.$refs.follow.listData,
          FriendGroupData: this.$refs.buddyGroup.buddyGroupList,
          BankSettingData: this.$refs.bankSetting.bankInfo,
          templateId: this.templateId,
          loginStep:{
            id: this.stepId ? this.stepId : 0,
            account: this.$user.loginAccount,
            templateId: this.templateId,
            step: 4
          }
        }
        console.log('data', data)
        this.$api.post('/member-reversion/indivi/saveIndividInfo', data).then(response => {
          console.log('save response', response)
          if (response.code === 200) {
            this.$Message.success('保存成功！')
            this.$router.push({
              path: '/auth/step5',
              query: {
                templateId: this.templateId
              }
            })
          } else if (response.code === 400) {
            this.$Message.error('验证码错误！')
          } else if (response.code === 500) {
            this.$Message.error('保存失败！')
          }
        }).catch(error => {
          this.$Message.error('服务器异常！')
        })
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.back-btn {
  background-color: #9B9B9B;
  border-color: #9B9B9B;
  &:hover {
    background-color: #9B9B9B;
    border-color: #9B9B9B;
  }
}
.layout {
  width: 1000px;
  margin: auto;
  margin-top: 20px;
}
</style>
