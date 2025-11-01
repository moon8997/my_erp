import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import Register from '../views/Register.vue';
import AddProduct from '../views/AddProduct.vue';
import AddCustomer from '../views/AddCustomer.vue';
import AddOrder from '../views/AddOrder.vue';
import Header from '../components/Header.vue';
import NotFound from '../views/NotFound.vue';

const routes = [
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/add-product', component: AddProduct },
  { path: '/add-customer', component: AddCustomer },
  { path: '/add-order', component: AddOrder },
  { path: '/header', component: Header },
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
