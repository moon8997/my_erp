import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import Register from '../views/Register.vue';
import ProductAdd from '../views/ProductAdd.vue';
import CustomerAdd from '../views/CustomerAdd.vue';
import OrderAdd from '../views/OrderAdd.vue';
import ProductList from '../views/ProductList.vue';
import CustomerList from '../views/CustomerList.vue';
import OrderList from '../views/OrderList.vue';
import Header from '../components/Header.vue';
import NotFound from '../views/NotFound.vue';
import ReceiptForm from '../views/ReceiptForm.vue';
import SampleReceipt from '../views/SampleReceipt.vue';
import CollectionPrep from '../views/CollectionPrep.vue';

const routes = [
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/product/add', component: ProductAdd },
  { path: '/customer/add', component: CustomerAdd },
  { path: '/order/add', component: OrderAdd },
  { path: '/product/list', component: ProductList },
  { path: '/customer/list', component: CustomerList },
  { path: '/order/list', component: OrderList },
  { path: '/header', component: Header },
  { path: '/receipt', component: ReceiptForm },
  { path: '/sample', component: SampleReceipt },
  { path: '/collection/prep', component: CollectionPrep },
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: NotFound }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 라우터 가드: 로그인 상태에 따라 접근 제어
router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
  const publicPaths = ['/login', '/register'];

  // 비로그인: 공개 경로 외에는 /login으로 이동
  if (!isLoggedIn && !publicPaths.includes(to.path)) {
    return next('/login');
  }

  // 로그인 상태: 로그인/회원가입 페이지 접근 차단
  if (isLoggedIn && publicPaths.includes(to.path)) {
    return next('/');
  }

  return next();
});

export default router;