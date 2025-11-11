# CaseNara ERP – 포트폴리오 README

이 문서는 CaseNara ERP 프로젝트를 GitHub 포트폴리오 형식으로 요약합니다. 프론트엔드·백엔드 아키텍처, 주요 기능, 데이터 모델, UI 라우트, API 요약, 실행 방법을 한 곳에서 빠르게 파악할 수 있도록 구성했습니다.

## 목차
- [프로젝트 개요](#프로젝트-개요)
- [기술 스택](#기술-스택)
- [주요 기능](#주요-기능)
- [UI 라우트](#ui-라우트)
- [백엔드 아키텍처](#백엔드-아키텍처)
- [데이터 모델](#데이터-모델)
- [API 요약](#api-요약)
- [실행 방법](#실행-방법)
- [배포 정보](#배포-정보)
- [스크린샷/데모](#스크린샷데모)
- [향후 개선](#향후-개선)
- [개인 역할/기여](#개인-역할기여)

## 프로젝트 개요
소규모 유통/판매 시나리오에 맞춘 ERP 웹 애플리케이션입니다. 상품 등록→고객 관리→주문/판매→청구/수금까지 기본 흐름을 지원하며, 간결한 UI와 명시적인 데이터 모델을 목표로 합니다.

## 기술 스택
- 프론트엔드: `Vue 3`, `Vite`, `Vue Router`
- 백엔드: `Spring Boot`, `MyBatis`
- 데이터베이스: `MySQL`(예시) – `sql/`에 초기 스키마 제공
- 기타: REST API, 로컬 개발 프록시(`/api -> http://localhost:8651`)

## 주요 기능
- 상품 관리: 등록/목록/검색, 재고/가격 관리
- 고객 관리: 등록/목록, 기본 연락처/등급
- 주문/판매: 주문 생성, 판매 리스트, 간단한 상태 관리
- 청구/수금: 청구서 발행, 수금 준비/기록
- 인증: 로그인/회원가입, 로그인 가드로 접근 제어

## UI 라우트
프론트엔드 라우트 구성은 `frontend/src/router/index.js`에 정의되어 있습니다.

```
/                  -> Portfolio(프로젝트 요약)
/portfolio         -> Portfolio
/login             -> Login
/register          -> Register
/product/add       -> ProductAdd
/customer/add      -> CustomerAdd
/order/add         -> OrderAdd
/product/list      -> ProductList
/customer/list     -> CustomerList
/order/list        -> OrderList
/bill              -> BillList
/receipt           -> ReceiptForm
/sample            -> SampleReceipt
/collection/prep   -> CollectionPrep
/*                 -> NotFound
```

## 백엔드 아키텍처
- 레이어 구성: `Controller` → `Service` → `Mapper(MyBatis)` → DB
- 주요 모듈(예시):
  - 상품/고객/주문/판매/청구서 도메인 컨트롤러 및 서비스
  - 스케줄러(청구서 정리, 알림 등 확장 가능)
  - 설정/보안: `CORS`, `로그인 세션/토큰`(필요 시)
- 프로젝트 루트: `caseNara/` (Spring Boot 애플리케이션)

## 데이터 모델
`sql/` 디렉터리에 초기 스키마가 제공됩니다.
- `case_nara_products.sql`: `id`, `name`, `sku`, `price`, `stock`, `supplier_id`
- `case_nara_customers.sql`: `id`, `name`, `phone`, `email`, `tier`
- `case_nara_sales.sql`: `id`, `order_id`, `product_id`, `qty`, `amount`, `status`
- `case_nara_bills.sql`: `id`, `sale_id`, `due_date`, `amount`, `status`
- `case_nara_suppliers.sql`: 공급처 정보(선택)
- `case_nara_menus.sql`: 네비게이션/권한 기반 메뉴(확장용)
- `case_nara_account.sql`: 사용자 계정/권한(확장용)

## API 요약
프록시: Vite 개발 서버는 `/api`를 `http://localhost:8651`으로 전달합니다(`frontend/vite.config.js`).
- 인증: `POST /api/auth/login`, `POST /api/auth/register`
- 상품: `GET /api/products`, `POST /api/products`, `GET /api/products/:id`
- 고객: `GET /api/customers`, `POST /api/customers`, `GET /api/customers/:id`
- 주문: `POST /api/orders`, `GET /api/orders`, `GET /api/orders/:id`
- 판매: `GET /api/sales`, `POST /api/sales`
- 청구/수금: `GET /api/bills`, `POST /api/bills`, `POST /api/collection`
- 메뉴: `GET /api/menus` (헤더 네비게이션에 사용)


## 실행 방법

### 1) 백엔드 (Spring Boot)
```bash
cd caseNara
# Maven 빌드 및 실행 (예)
./mvnw spring-boot:run
# Windows
mvnw.cmd spring-boot:run
```
- 애플리케이션 시작 후 API 베이스 URL: `http://localhost:8651`
- DB 초기화: `sql/` 폴더의 스키마를 실행(로컬 MySQL 등)

### 2) 프론트엔드 (Vue 3 + Vite)
```bash
cd frontend
npm install
npm run dev
```
- 개발 서버: `http://localhost:5173`
- 프록시가 `/api` 요청을 백엔드로 전달합니다. 백엔드가 미기동이면 헤더 메뉴 요청(`/api/menus`) 실패 로그가 보이지만, 포트폴리오/정적 페이지는 정상 표시됩니다.

### 3) 프로덕션 빌드/배포 (Vue)
```bash
cd frontend
npm install
npm run build
```
- 빌드 결과: `frontend/dist/` 폴더
- 운영 서버(Nginx 등)에서 `dist`를 정적 파일로 서빙하고, `/api`는 Spring Boot로 리버스 프록시합니다.

## 배포 정보
- 공개 URL: `https://casenara.kro.kr`
- 리버스 프록시: 운영 환경에서는 Vite 개발 프록시 대신 Nginx를 사용해 `/api`를 백엔드로 전달합니다.

### Nginx 설정 예시
> 실제 경로와 인증서 위치는 서버 환경에 맞게 조정하세요.

```nginx
server {
  listen 80;
  server_name casenara.kro.kr;

  # HTTPS를 사용한다면 80에서 443으로 리다이렉트
  return 301 https://$host$request_uri;
}

server {
  listen 443 ssl;
  server_name casenara.kro.kr;

  # 인증서 (Let's Encrypt 등)
  ssl_certificate     /etc/letsencrypt/live/casenara.kro.kr/fullchain.pem;
  ssl_certificate_key /etc/letsencrypt/live/casenara.kro.kr/privkey.pem;

  # 정적 프론트엔드 (Vue build 결과)
  root /var/www/casenara/dist;
  index index.html;

  location / {
    try_files $uri $uri/ /index.html;
  }

  # API는 Spring Boot로 프록시
  location /api/ {
    proxy_pass         http://127.0.0.1:8651/;
    proxy_set_header   Host $host;
    proxy_set_header   X-Real-IP $remote_addr;
    proxy_set_header   X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header   X-Forwarded-Proto $scheme;
  }
}
```

### Spring Boot 운영 설정 팁
- 포트: `server.port=8651`
- CORS: 운영 도메인(`https://casenara.kro.kr`)을 허용 원본으로 설정
- DB: 운영 DB 연결 정보 분리(`application-prod.properties` 등)

## 스크린샷/데모
- 홈/포트폴리오: 프로젝트 요약/기능 카드/라우트 목록이 표시됨
- 상품/고객/주문/청구 리스트 화면: 데이터 테이블/폼
- 데모 시나리오: 상품 등록 → 주문 생성 → 판매 기록 → 청구서 발행 → 수금 준비

> 이미지 자산은 `docs/images/` 등에 추가하여 README에 삽입할 수 있습니다.

## 향후 개선
- 권한(RBAC)과 메뉴/엔드포인트 접근 제어 강화
- 이미지 업로드(상품 이미지) 및 CDN/S3 연동
- 리포트/PDF(청구서/거래명세서) 생성 및 다운로드
- 대량 데이터 페이징/가상 스크롤, 캐싱 최적화
- 테스트/CI 파이프라인 정비 및 품질 지표 추가

## 개인 역할/기여
- 설계: 도메인 모델링(상품/고객/주문/청구/판매) 및 API 설계
- 구현: Vue 3 기반 화면, 라우트 구성, Spring Boot + MyBatis 서비스 로직
- 운영: 개발 프록시/환경 구성, 로컬 DB 초기화 및 샘플 데이터 준비
- 문서화: README(이 문서)로 프로젝트 전체를 빠르게 파악 가능하도록 정리

---

문의나 개선 제안은 Issue/PR로 환영합니다.
