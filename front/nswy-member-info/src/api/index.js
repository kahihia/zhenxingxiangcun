import axios from 'axios'
import config from './config'

// 取消请求
let CancelToken = axios.CancelToken

// 添加请求拦截器
axios.defaults.withCredentials = true
axios.interceptors.request.use(
  config => {
    // 得到参数中的 requestName 字段，用于决定下次发起请求，取消对应的 相同字段的请求
    // 如果没有 requestName 就默认添加一个 不同的时间戳
    let requestName
    if (config.method === 'post') {
      if (config.data && config.data.requestName) {
        requestName = config.data.requestName
      } else {
        requestName = new Date().getTime()
      }
    } else {
      if (config.params && config.params.requestName) {
        requestName = config.params.requestName
      } else {
        requestName = new Date().getTime()
      }
    }
    // 判断，如果这里拿到的参数中的 requestName 在上一次请求中已经存在，就取消上一次的请求
    if (requestName) {
      if (axios[requestName] && axios[requestName].cancel) {
        axios[requestName].cancel()
      }
      config.cancelToken = new CancelToken(c => {
        axios[requestName] = {}
        axios[requestName].cancel = c
      })
    }
    return config
  },
  error => Promise.reject(error)
)
// 添加响应拦截器
axios.interceptors.response.use(
  config => {
    // 返回请求正确的结果
    return config
  },
  error => {
    // 错误的请求结果处理，这里的代码根据后台的状态码来决定错误的输出信息
    // if (error && error.response) {
    //   switch (error.response.status) {
    //     case 400:
    //       error.message = '错误请求'
    //       break
    //     case 401:
    //       error.message = '未授权，请重新登录'
    //       break
    //     case 403:
    //       error.message = '拒绝访问'
    //       break
    //     case 404:
    //       error.message = '请求错误,未找到该资源'
    //       break
    //     case 405:
    //       error.message = '请求方法未允许'
    //       break
    //     case 408:
    //       error.message = '请求超时'
    //       break
    //     case 500:
    //       error.message = '服务器端出错'
    //       break
    //     case 501:
    //       error.message = '网络未实现'
    //       break
    //     case 502:
    //       error.message = '网络错误'
    //       break
    //     case 503:
    //       error.message = '服务不可用'
    //       break
    //     case 504:
    //       error.message = '网络超时'
    //       break
    //     case 505:
    //       error.message = 'http版本不支持该请求'
    //       break
    //     default:
    //       error.message = `连接错误${error.response.status}`
    //   }
    // } else {
    //   error.message = '连接到服务器失败'
    // }
    return Promise.reject(error.message)
  }
)

function checkStatus (response) {
  if (response.status === 200 || response.status === 304) {
    return response
  }
  return {
    data: {
      code: false,
      msg: response.statusText,
      data: response.statusText
    }
  }
}

function checkCode (res) {
  // if (res.data.code === 200) {
  // }
  return res.data
}

export default {
  post (url, data) {
    return axios({
      method: 'post',
      url: config + url,
      data: data,
      timeout: 30000,
      withCredentials: true,
      headers: {
        'X-Requested-With': 'XMLHttpRequest',
        'Content-Type': 'application/json; charset=UTF-8'
      }
    })
      .then(checkStatus)
      .then(checkCode)
  },
  get (url, params) {
    return axios({
      method: 'get',
      url: config + url,
      params,
      timeout: 30000,
      withCredentials: true,
      headers: {
        'X-Requested-With': 'XMLHttpRequest',
        'Content-Type': 'application/json;charset=utf-8'
      }
    })
      .then(checkStatus)
      .then(checkCode)
  }
}
