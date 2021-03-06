import Vue from 'vue';
import iView from 'iview';
import router from './router';
import App from './app.vue';
import Vuex from 'vuex';
import vueScrollActive from 'vue-scrollactive'
import store from './store/index.js'
import Print from 'vue-print-nb'
import '../theme/dist/iview.css';
import './scss/WMlib.scss'
// 实际打包时应该不引入mock
import env from '~src/api/env';

import moment from 'moment'; //日期格式化

import config from '~src/api/config';

import api from '~api/index';
import {toPortals, toMap, delMapData} from './utils/toPortals';

let url = {};
env.nodeENV === 'development' ? url = config.dev : url = config.prod;
/* eslint-disable */
env.nodeENV === 'development' ? require('~src/mock') : ''
console.log('url: ', url);
Vue.prototype.moment = moment;
Vue.prototype.$api = api;
Vue.prototype.$toMap = toMap;
Vue.prototype.$delMapData = delMapData;
Vue.prototype.$url = url;
Vue.prototype.$map = null
Vue.prototype.$toPortals = toPortals
Vue.prototype.$user = JSON.parse(sessionStorage.getItem(sessionStorage.getItem('key')))
Vue.use(iView);
Vue.use(Vuex);
Vue.use(Print)
Vue.use(vueScrollActive)

router.beforeEach((to, from, next) => {
  if (to.path !== '/index') {
    window.scrollTo(0, 0);
  }
  if (to.meta.auth) {
    const user = sessionStorage.getItem(sessionStorage.getItem('key'))
    if (user === null) {
      iView.Message.error('请先登录')
      next({path: '/index'});
    } else {
      next();
    }
  } else {
    next();
  }
});

router.afterEach((to, from, next) => {
  iView.LoadingBar.finish();
  if (from.meta !== {}) {
    if (from.meta.isScroll) {
      window.scrollTo(0, 0);
    }
  }
});

new Vue({
  el: '#app',
  router: router,
  store: store,
  render: h => h(App)
});
