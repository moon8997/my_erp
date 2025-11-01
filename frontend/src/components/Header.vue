<template>
  <header class="header-container">
    <div class="header-content">
      <!-- 로고/브랜드 영역 -->
      <div class="brand">
        <h1 class="brand-title">CaseNara</h1>
      </div>
      
        <!-- 네비게이션 메뉴 -->
        <nav class="navigation">
          <ul class="nav-list">
            <li 
              v-for="parent in menuTree" 
              :key="parent.menuCode" 
              class="nav-item"
              @mouseenter="openDropdown(parent.menuCode)" 
              @mouseleave="closeDropdown(parent.menuCode)"
            >
              <!-- 부모가 자식을 갖는 경우 드롭다운 -->
              <template v-if="parent.children && parent.children.length">
                <span class="nav-link dropdown-toggle" :class="{ active: isActiveParent(parent) }">
                  <span class="nav-icon">{{ getMenuIcon(parent.menuCode) }}</span>
                  <span class="nav-text">{{ parent.menuName }}</span>
                </span>
                <ul class="dropdown-menu" v-show="isDropdownOpen(parent.menuCode)">
                  <li v-for="child in parent.children" :key="child.menuCode">
                    <router-link 
                      v-if="child.endpoint && child.endpoint !== '(null)'" 
                      :to="child.endpoint" 
                      class="dropdown-item" 
                      :class="{ active: $route.path === child.endpoint }"
                    >
                      <span class="nav-text">{{ child.menuName }}</span>
                    </router-link>
                    <span v-else class="dropdown-item disabled">
                      <span class="nav-text">{{ child.menuName }}</span>
                    </span>
                  </li>
                </ul>
              </template>

              <!-- 자식이 없는 일반 메뉴 -->
              <template v-else>
                <router-link 
                  v-if="parent.endpoint && parent.endpoint !== '(null)'" 
                  :to="parent.endpoint" 
                  class="nav-link" 
                  :class="{ active: $route.path === parent.endpoint }"
                >
                  <span class="nav-icon">{{ getMenuIcon(parent.menuCode) }}</span>
                  <span class="nav-text">{{ parent.menuName }}</span>
                </router-link>
                <span v-else class="nav-link disabled">
                  <span class="nav-icon">{{ getMenuIcon(parent.menuCode) }}</span>
                  <span class="nav-text">{{ parent.menuName }}</span>
                </span>
              </template>
            </li>
          </ul>
        </nav>
      
      <!-- 사용자 정보 및 로그아웃 -->
      <div v-if="isLoggedIn" class="user-section">
        <button @click="logout" class="logout-btn">로그아웃</button>
      </div>
      
      <!-- 모바일 메뉴 토글 -->
      <button @click="toggleMobileMenu" class="mobile-menu-toggle">
        <span class="hamburger"></span>
        <span class="hamburger"></span>
        <span class="hamburger"></span>
      </button>
    </div>
    
    <!-- 모바일 메뉴 -->
    <div v-if="showMobileMenu" class="mobile-menu">
      <ul class="mobile-nav-list">
        <li v-for="parent in menuTree" :key="parent.menuCode" class="mobile-nav-item">
          <template v-if="parent.children && parent.children.length">
            <div class="mobile-nav-link" @click="toggleDropdown(parent.menuCode)">
              <span class="nav-icon">{{ getMenuIcon(parent.menuCode) }}</span>
              <span class="nav-text">{{ parent.menuName }}</span>
            </div>
            <ul class="mobile-dropdown-menu" v-show="isDropdownOpen(parent.menuCode)">
              <li v-for="child in parent.children" :key="child.menuCode" class="mobile-nav-item">
                <router-link 
                  v-if="child.endpoint && child.endpoint !== '(null)'" 
                  :to="child.endpoint" 
                  class="mobile-nav-link child" 
                  @click="closeMobileMenu"
                >
                  <span class="nav-text">{{ child.menuName }}</span>
                </router-link>
                <span v-else class="mobile-nav-link disabled child">
                  <span class="nav-text">{{ child.menuName }}</span>
                </span>
              </li>
            </ul>
          </template>
          <template v-else>
            <router-link 
              v-if="parent.endpoint && parent.endpoint !== '(null)'" 
              :to="parent.endpoint" 
              class="mobile-nav-link" 
              @click="closeMobileMenu"
            >
              <span class="nav-icon">{{ getMenuIcon(parent.menuCode) }}</span>
              <span class="nav-text">{{ parent.menuName }}</span>
            </router-link>
            <span v-else class="mobile-nav-link disabled">
              <span class="nav-icon">{{ getMenuIcon(parent.menuCode) }}</span>
              <span class="nav-text">{{ parent.menuName }}</span>
            </span>
          </template>
        </li>
        <li v-if="isLoggedIn" class="mobile-nav-item mobile-user-section">
          <div class="mobile-user-info">
            <button @click="logout" class="mobile-logout-btn">로그아웃</button>
          </div>
        </li>
      </ul>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const isLoggedIn = ref(false)
const userId = ref('')
const showMobileMenu = ref(false)
const menus = ref([])

// 드롭다운 열림 상태 관리
const openDropdowns = ref({})
const isDropdownOpen = (code) => !!openDropdowns.value[code]
const openDropdown = (code) => { openDropdowns.value[code] = true }
const closeDropdown = (code) => { openDropdowns.value[code] = false }
const toggleDropdown = (code) => { openDropdowns.value[code] = !openDropdowns.value[code] }

// 메뉴 아이콘 매핑
const getMenuIcon = (menuCode) => {
  const iconMap = {
    1: '🏠', // 대시보드
    2: '📋', // 주문등록
    3: '📦', // 상품관리
    4: '🏢', // 거래처등록  
    5: '💰'  // 수금업무
  }
  return iconMap[menuCode] || '📄'
}

// flat 메뉴를 부모-자식 트리로 변환
const menuTree = computed(() => {
  const parents = []
  const map = new Map()
  const sorted = [...menus.value].sort((a, b) => {
    const ad = a.displayOrder ?? 0
    const bd = b.displayOrder ?? 0
    return ad - bd
  })

  // 부모 수집
  sorted.forEach(item => {
    if (item.parentCode == null) {
      const node = { ...item, children: [] }
      map.set(item.menuCode, node)
      parents.push(node)
    }
  })

  // 자식 연결
  sorted.forEach(item => {
    if (item.parentCode != null && map.has(item.parentCode)) {
      map.get(item.parentCode).children.push(item)
    }
  })

  // 자식 정렬
  parents.forEach(p => p.children.sort((a, b) => {
    const ad = a.displayOrder ?? 0
    const bd = b.displayOrder ?? 0
    return ad - bd
  }))

  return parents
})

// 부모 활성 상태 (자식 중 현재 라우트가 존재하는지)
const isActiveParent = (parent) => {
  return parent.children && parent.children.some(c => c.endpoint && router.currentRoute.value.path === c.endpoint)
}

// 메뉴 데이터 가져오기
const fetchMenus = async () => {
  try {
    const response = await axios.get('/api/menus')
    menus.value = response.data
  } catch (error) {
    console.error('메뉴 데이터를 가져오는데 실패했습니다:', error)
    // 에러 발생 시 기본 메뉴 사용
    menus.value = [
      { menuCode: 1, menuName: '대시보드', displayOrder: 0, endpoint: '/', parentCode: null },
      { menuCode: 2, menuName: '주문등록', displayOrder: 1, endpoint: '/add-order', parentCode: null },
      { menuCode: 3, menuName: '상품관리', displayOrder: 2, endpoint: null, parentCode: null },
      { menuCode: 6, menuName: '상품등록', displayOrder: 2, endpoint: '/add-product', parentCode: 3 },
      { menuCode: 7, menuName: '상품리스트', displayOrder: 2, endpoint: '/list-product', parentCode: 3 },
      { menuCode: 4, menuName: '거래처등록', displayOrder: 3, endpoint: '/add-customer', parentCode: null },
      { menuCode: 5, menuName: '수금업무', displayOrder: 4, endpoint: null, parentCode: null }
    ]
  }
}

// 로그인 상태 확인
const checkLoginStatus = () => {
  isLoggedIn.value = localStorage.getItem('isLoggedIn') === 'true'
  userId.value = localStorage.getItem('userId') || ''
  
  // 로그인 상태일 때만 메뉴 데이터 가져오기
  if (isLoggedIn.value) {
    fetchMenus()
  }
}

// 로그아웃 처리
const logout = async () => {
  try {
    // 서버에 로그아웃 요청
    await axios.post('/api/account/logout')
    
    // 로컬 스토리지 정리
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('userId')
    menus.value = [] // 메뉴 데이터 초기화
    isLoggedIn.value = false
    showMobileMenu.value = false
    
    // 로그인 페이지로 이동
    router.push('/login')
  } catch (error) {
    console.error('로그아웃 중 오류 발생:', error)
    
    // 서버 요청이 실패해도 클라이언트 측 정리는 수행
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('userId')
    menus.value = []
    isLoggedIn.value = false
    showMobileMenu.value = false
    router.push('/')
  }
}

// 모바일 메뉴 토글
const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value
}

// 모바일 메뉴 닫기
const closeMobileMenu = () => {
  showMobileMenu.value = false
}

// 컴포넌트 마운트 시 로그인 상태 확인
onMounted(() => {
  checkLoginStatus()
  
  // localStorage 변경 감지 (다른 탭에서 로그인/로그아웃 시)
  window.addEventListener('storage', checkLoginStatus)
})

// 컴포넌트 언마운트 시 이벤트 리스너 제거
onUnmounted(() => {
  window.removeEventListener('storage', checkLoginStatus)
})
</script>

<style scoped>
.header-container {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 64px;
}

/* 브랜드 영역 */
.brand {
  flex-shrink: 0;
}

.brand-title {
  color: white;
  font-size: 24px;
  font-weight: bold;
  margin: 0;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

/* 네비게이션 */
.navigation {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-list {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: 8px;
}

.nav-item {
  position: relative;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.3s ease;
  font-weight: 500;
  white-space: nowrap;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  transform: translateY(-1px);
}

.nav-link.active {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.nav-link.disabled {
  color: rgba(255, 255, 255, 0.5);
  cursor: not-allowed;
  opacity: 0.6;
}

.nav-link.disabled:hover {
  background: none;
  transform: none;
}

.nav-icon {
  font-size: 16px;
}

.nav-text {
  font-size: 14px;
}

/* 드롭다운 메뉴 */
.dropdown-toggle::after {
  content: '▾';
  margin-left: 6px;
  font-size: 12px;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  min-width: 180px;
  background: rgba(255, 255, 255, 0.95);
  color: #333;
  border-radius: 8px;
  box-shadow: 0 8px 20px rgba(0,0,0,0.12);
  padding: 8px 0;
  list-style: none;
  margin: 0;
  border: 1px solid rgba(0,0,0,0.08);
  overflow: hidden;
  z-index: 1100;
}

.dropdown-menu li { list-style: none; }

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  color: #333;
  text-decoration: none;
  transition: background 0.2s ease;
}

.dropdown-item:hover {
  background: rgba(102, 126, 234, 0.14);
}

.dropdown-item.active {
  font-weight: 600;
}

.dropdown-item.disabled {
  color: #999;
  cursor: not-allowed;
}

/* 사용자 섹션 */
.user-section {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.user-info {
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  font-size: 14px;
}

.logout-btn {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-1px);
}

/* 모바일 메뉴 토글 */
.mobile-menu-toggle {
  display: none;
  flex-direction: column;
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  gap: 4px;
}

.hamburger {
  width: 24px;
  height: 3px;
  background: white;
  border-radius: 2px;
  transition: all 0.3s ease;
}

/* 모바일 메뉴 */
.mobile-menu {
  display: none;
  background: rgba(102, 126, 234, 0.95);
  backdrop-filter: blur(10px);
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.mobile-nav-list {
  list-style: none;
  margin: 0;
  padding: 16px 0;
}

.mobile-nav-item {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.mobile-nav-item:last-child {
  border-bottom: none;
}

.mobile-nav-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  transition: all 0.3s ease;
}

.mobile-nav-link:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.mobile-nav-link.disabled {
  color: rgba(255, 255, 255, 0.5);
  cursor: not-allowed;
}

/* 모바일 드롭다운 하위 메뉴 */
.mobile-dropdown-menu {
  background: rgba(255, 255, 255, 0.08);
  list-style: none;
  margin: 0;
  padding: 4px 0;
}

.mobile-nav-link.child {
  padding-left: 36px;
}

.mobile-dropdown-menu .mobile-nav-item {
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
}

.mobile-user-section {
  border-top: 2px solid rgba(255, 255, 255, 0.2);
  margin-top: 8px;
  padding-top: 8px;
}

.mobile-user-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  color: rgba(255, 255, 255, 0.9);
}

.mobile-logout-btn {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
  }
  
  .navigation {
    display: none;
  }
  
  .user-section {
    display: none;
  }
  
  .mobile-menu-toggle {
    display: flex;
  }
  
  .mobile-menu {
    display: block;
  }
  
  .brand-title {
    font-size: 20px;
  }
}

@media (max-width: 480px) {
  .header-content {
    padding: 0 12px;
    height: 56px;
  }
  
  .brand-title {
    font-size: 18px;
  }
  
  .mobile-nav-link {
    padding: 14px 16px;
  }
  
  .mobile-user-info {
    padding: 14px 16px;
  }
}

/* 접근성 개선 */
@media (prefers-reduced-motion: reduce) {
  .nav-link,
  .logout-btn,
  .mobile-nav-link,
  .hamburger {
    transition: none;
  }
}

/* 다크 모드 지원 */
@media (prefers-color-scheme: dark) {
  .header-container {
    background: linear-gradient(135deg, #1a202c 0%, #2d3748 100%);
  }
  
  .mobile-menu {
    background: rgba(26, 32, 44, 0.95);
  }
}
</style>
