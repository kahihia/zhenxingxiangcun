<template>
  <div class="pd20">
    <Title :title="title" edit :id="modeId" :yearId="yearId" :templateId="templateId" @left-refresh="leftRefresh" />
    <Row class="mt40">
        <Col span="12">
            <Form>
                <Form-item label="权限">
                    <i-switch  v-model="status" size="large">
                        <span slot="open">公开</span>
                        <span slot="close">隐藏</span>
                    </i-switch>
                </Form-item>
            </Form>
        </Col>
    </Row>
    <Card v-for="(item,index) in data" :key="index" class="mb40">
        <Form :ref="`formItem${index}`" :model="item" label-position="left" :label-width="100" :rules="formItemInline">
            <Row>
                <Col span="12" offset="12" class="tr">
                    <div class="btn-toolbar" style="margin-top: -10px;">
                        <Button type="text" @click="edit(index)" v-if="!item.isAdd"><Icon type="trash-a" size="16" class="pr5"></Icon> 编辑</Button> 
                        <Button type="text" @click="del(item, index)" v-if="item.isAdd && data.length > 1"><Icon type="trash-a" size="16" class="pr5"></Icon> 删除</Button> 
                    </div>
                </Col>
            </Row>
            <Row :gutter="32">
                <Col span="8">
                    <Form-item label="开户银行">
                        <Select v-model="item.bank" @on-change="change" :disabled="!item.isAdd">
                            <Option v-for="(item,index) in banks" :key="index" :value="item.value">{{ item.label }}</Option>
                        </Select>
                    </Form-item>
                </Col>
                <Col span="8">
                    <Form-item label="支行名称">
                        <Input v-model="item.bankName" :maxlength="20" :disabled="!item.isAdd" @on-change="change" />
                    </Form-item>
                </Col>
                <Col span="8">
                    <Form-item label="银行卡号" prop="bankCardNumber">
                        <Input v-model="item.bankCardNumber" :maxlength="19" :disabled="!item.isAdd" @on-change="change" />
                    </Form-item>
                </Col>
            </Row>
            <Row :gutter="32">
                <Col span="8">
                    <Form-item label="开户人">
                        <Input v-model="item.accountHolder" :maxlength="20" :disabled="!item.isAdd" @on-change="change" />
                    </Form-item>
                </Col>
            </Row>
        </Form>
        <div class="tc">
            <Button type="primary" v-if="item.isAdd" @click="save(item, index)">保存</Button>
        </div>
    </Card>
    <Form label-position="left" :label-width="150" class="pb20 mt40">
        <Row :gutter="32">
            <Col span="12">
                <Button type="success" ghost @click="handleAdd" icon="md-add" class="btn-light-primary">添加</Button>
            </Col>
        </Row>
    </Form>
    <Title title="文字预览"/>
    <div class="pd20 tc pt30">
        <Input v-model="preview" type="textarea" :autosize="{minRows: 3,maxRows: 5}" />
        <Button type="primary" v-if="isLoading" class="mt40">保存</Button>
        <Button type="primary" v-else @click="handleSave()" class="mt40">保存</Button>
    </div>
  </div>
</template>
<script>
    import vuiUpload from '~components/vui-upload'
    import Title from '../../components/title'
    import { isBankCard } from '../../../../utils/validate'
    export default {
        components: {
            vuiUpload,
            Title
        },
        props: {
            modeId: {
                type: String
            },
            yearId: {
                type: String
            },
            appId: {
                type: String
            }
        },
        data () {
            return {
                title: '银行账号信息',
                formItemInline: {
                    bankCardNumber: [
                        { validator: isBankCard, trigger: 'blur' }
                    ]
                },
                data: [
                    {
                        bank: '',
                        bankName: '',
                        bankCardNumber: '',
                        accountHolder: '',
                        isAdd: true,
                        status: true
                    }
                ],
                banks: [
                    { label: '工商银行', value: '工商银行' },
                    { label: '农业银行', value: '农业银行' },
                    { label: '中国银行', value: '中国银行' },
                    { label: '建设银行', value: '建设银行' },
                    { label: '交通银行', value: '交通银行' },
                    { label: '邮政储蓄银行', value: '邮政储蓄银行' },
                    { label: '中信银行', value: '中信银行' },
                    { label: '光大银行', value: '光大银行' },
                    { label: '华夏银行', value: '华夏银行' },
                    { label: '广发银行', value: '广发银行' },
                    { label: '平安银行', value: '平安银行' },
                    { label: '招商银行', value: '招商银行' },
                    { label: '浦发银行', value: '浦发银行' },
                    { label: '兴业银行', value: '兴业银行' },
                    { label: '民生银行', value: '民生银行' },
                    { label: '恒丰银行', value: '恒丰银行' },
                    { label: '浙商银行', value: '浙商银行' },
                    { label: '渤海银行', value: '渤海银行' },
                    { label: '其他银行', value: '其他银行' }
                ],
                preview: '',
                index: 0,
                status: true,
                id: '',
                faninceStatusId: '',
                templateId: '',
                isLoading: true,
                saveisloading: false
            }
        },
        watch: {
            modeId: {
                handler (newValue, oldValue) {
                    this.init()
                    this.initTitle()
                },
                deep: true
            }
        },
        created () {
            this.templateId = this.$route.query.templateId
            if (this.modeId !== '' && this.modeId !== undefined) {
                this.init()
                this.initTitle()
            }
        },
        methods: {
            initTitle () {
                this.$api.post('/member-reversion/user/perfect/findTableHead', {
                    account: this.$user.loginAccount,
                    yearId: this.yearId,
                    dictId: this.modeId,
                    templateId: this.templateId
                }).then(response => {
                    if (response.code === 200) {
                        if (response.data.propertyName) {
                            this.title = response.data.propertyName
                        }
                    }
                }).catch(error => {
                    this.$Message.error('服务器异常！')
                })
            },
            // 初始化加载数据
            init (type = 0) {
                this.$api.post('/member-reversion/finance/findBankAccountInfo', {
                    account: this.$user.loginAccount,
                    yearId: this.yearId,
                    parentId: this.modeId,
                    templateId: this.templateId
                }).then(response => {
                    if (response.code === 200) {
                        this.isLoading = false
                        this.status = response.data.status
                        /* if (response.data.propertyName && response.data.propertyName !== '') {
                            this.title = response.data.propertyName
                        } */
                        if (response.data.faninceStatusId && response.data.faninceStatusId !== '') {
                            this.faninceStatusId = response.data.faninceStatusId
                        }
                        if (response.data.textPreview && response.data.textPreview.textPreview !== '') {
                            this.preview = response.data.textPreview.textPreview
                            this.id = response.data.textPreview.id
                        }
                        if (response.data.bankAccount.length !== 0) {
                            this.data = []
                            response.data.bankAccount.forEach((element, index) => {
                                this.data.push({
                                    id: element.id,
                                    bank: element.bank,
                                    bankName: element.bankName,
                                    bankCardNumber: element.bankCardNumber,
                                    accountHolder: element.accountHolder,
                                    isAdd: false
                                })
                            })
                        }
                        if (type === 1) {
                            this.change()
                        }
                    }
                }).catch(error => {
                    this.$Message.error('服务器异常！')
                })
            },
            //增加
            handleAdd () {
                this.data.push({
                    bank: '',
                    bankName: '',
                    bankCardNumber: '',
                    accountHolder: '',
                    isAdd: true,
                    status: true
                })
                this.index = this.data.length - 1
            },
            edit (index) {
                this.data[index].isAdd = true
                this.index = index
            },
            del (item, index) {
                this.$Modal.confirm({
                    title: '操作提示',
                    content: '是否确认删除？',
                    onOk:()=>{
                        if (item.id !== undefined && item.id !== '') {
                            this.$api.post('/member-reversion/finance/deleteBankAccountInfo', {
                                id: item.id
                            }).then(response => {
                                if (response.code === 200) {
                                    this.$Message.success('删除成功！')
                                    this.data.splice(index, 1)
                                    this.change()
                                }
                            }).catch(error => {
                                this.$Message.error('服务器异常！')
                            })
                        } else {
                            this.$Message.success('删除成功！')
                            this.data.splice(index, 1)
                            this.change()
                        }
                    },
                    okText:'确定',
                    cancelText:'取消'
                })
            },
            save (item, index) {
                if (!this.saveisloading) {
                    this.saveisloading = true
                    this.$refs[`formItem${index}`][0].validate(v => {
                        if (v) {
                            this.$api.post('/member-reversion/finance/saveBankAccountInfo', {
                                account: this.$user.loginAccount,
                                yearId: this.yearId,
                                parentId: this.modeId,
                                status: this.status,
                                faninceStatusId: this.faninceStatusId === '' ? 0 : this.faninceStatusId,
                                templateId: this.templateId,
                                bankAccount: {
                                    id: item.id === '' || item.id === undefined ? 0 : item.id,
                                    bank: item.bank,
                                    bankName: item.bankName,
                                    bankCardNumber: item.bankCardNumber,
                                    accountHolder: item.accountHolder
                                }
                            }).then(response => {
                                this.saveisloading = fasle
                                if (response.code === 200) {
                                    this.$Message.success('保存成功！')
                                    this.init(1)
                                }
                            }).catch(error => {
                                this.$Message.error('服务器异常！')
                            })
                        } else {
                            this.saveisloading = false
                            this.$Message.error('请核对表单信息')
                        }
                    })
                }
            },
            handleSave () {
                this.isLoading = true
                this.$api.post('/member-reversion/finance/saveTextPreview', {
                    textPreview: {
                        account: this.$user.loginAccount,
                        yearId: this.yearId,
                        parentId: this.modeId,
                        id: this.id === '' || this.id === undefined ? 0 : this.id,
                        textPreview: this.preview,
                        isComplete: this.data.length === 0 ? false : true,
                        templateId: this.templateId
                    }
                }).then(response => {
                    if (response.code === 200) {
                        this.$Message.success('保存成功！')
                        this.init()
                        this.$emit('on-save')
                    }
                }).catch(error => {
                    this.$Message.error('服务器异常！')
                })
            },
            change () {
                this.preview = ''
                this.data.forEach(element => {
                    if (element.accountHolder && element.accountHolder !== '') {
                        this.preview += `${element.accountHolder}，`
                    }
                    if (element.bankCardNumber && element.bankCardNumber !== '') {
                        this.preview += `银行卡号：${element.bankCardNumber}，`
                    }
                    if (element.bank && element.bank !== '' || element.bankName && element.bankName !== '') {
                        this.preview += `开户银行：`
                        if  (element.bank && element.bank !== '') {
                            this.preview += `${element.bank}，`
                        }
                        if  (element.bankName && element.bankName !== '') {
                            this.preview += `${element.bankName}，`
                        }
                    }
                    this.preview = this.preview.substring(0, this.preview.length - 1) + '；'
                })
                this.preview = this.preview.substring(0, this.preview.length - 1) + '。'
            },
            leftRefresh () {
                this.$emit('left-refresh')
            }
        }
    }
</script>
<style lang="scss" scoped>
</style>