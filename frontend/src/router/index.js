import { createRouter, createWebHistory } from 'vue-router'
import StockListView from '../views/StockListView.vue'
import StockPriceView from '../views/StockPriceView.vue'

const router = createRouter({
  history: createWebHistory(),

  routes: [
    {
      path: '/',
      name: 'stocks',
      component: StockListView
    },
    {
      path: '/stocks/:code',
      name: 'stockPrices',
      component: StockPriceView
    }
  ]
})

export default router
