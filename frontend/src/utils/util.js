// 한글 초성 검색 유틸리티
const CHO = ['ㄱ','ㄲ','ㄴ','ㄷ','ㄸ','ㄹ','ㅁ','ㅂ','ㅃ','ㅅ','ㅆ','ㅇ','ㅈ','ㅉ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'];
const HANGUL_BASE = 0xAC00;
const HANGUL_LAST = 0xD7A3;

/**
 * 문자열에서 한글 초성을 추출하는 함수
 * @param {string} str - 초성을 추출할 문자열
 * @returns {string} 추출된 초성 문자열
 */
export const getChosung = (str) => {
  if (!str) return '';
  let out = '';
  for (const ch of str) {
    const code = ch.codePointAt(0);
    if (code >= HANGUL_BASE && code <= HANGUL_LAST) {
      const idx = Math.floor((code - HANGUL_BASE) / 588);
      out += CHO[idx] || ch;
    } else {
      out += ch;
    }
  }
  return out;
};

/**
 * 검색어로 목록을 필터링하는 함수
 * @param {string} term - 검색어
 * @param {string[]} names - 검색 대상 목록
 * @param {string[]} chosungs - 검색 대상의 초성 목록
 * @returns {string[]} 필터링된 목록
 */
export const filterList = (term, names, chosungs) => {
  const q = term.trim();
  if (!q) return [];
  const qLower = q.toLowerCase();
  const qChosung = getChosung(q);
  const res = [];
  for (let i = 0; i < names.length; i++) {
    const name = names[i];
    const matchText = name.toLowerCase().includes(qLower);
    const matchCho = (chosungs[i] || '').includes(qChosung);
    if (matchText || matchCho) {
      res.push(name);
      if (res.length >= 10) break;
    }
  }
  return res;
};

/**
 * Date 객체를 YYYY-MM-DD 형식의 문자열로 변환하는 함수
 * @param {Date|string} date - 변환할 날짜
 * @returns {string} YYYY-MM-DD 형식의 날짜 문자열
 */
export const formatDate = (date) => {
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

/**
 * 오늘 날짜를 YYYY-MM-DD 형식의 문자열로 반환하는 함수
 * @returns {string} YYYY-MM-DD 형식의 오늘 날짜 문자열
 */
export const todayStr = () => formatDate(new Date());

/**
 * 깊은 복사 유틸리티
 * DataCloneError를 방지하기 위해 비복제 가능 값(함수, DOM 노드 등)은 제거합니다.
 * @param {any} obj - 복제할 객체
 * @returns {any} 복제된 객체
 */
export const deepClone = (obj) => {
  // 1) 가능한 경우 브라우저의 structuredClone을 시도하되, 실패하면 안전한 폴백 수행
  if (typeof structuredClone === 'function') {
    try {
      return structuredClone(obj);
    } catch (e) {
      // DataCloneError 등 발생 시 폴백으로 진행
      // console.debug('structuredClone 실패, JSON 기반 복제로 폴백합니다.', e);
    }
  }

  // 2) JSON 기반 폴백: 비직렬화 값들을 제거/단순화
  const replacer = (_key, value) => {
    // 함수, 심볼 등은 제거
    if (typeof value === 'function' || typeof value === 'symbol') return undefined;

    // DOM 노드나 Window/Document 같은 브라우저 객체는 제거
    const tag = Object.prototype.toString.call(value);
    if (tag === '[object Window]' || tag === '[object Document]' || tag === '[object HTMLDocument]') {
      return undefined;
    }
    if (tag.startsWith('[object HTML') || tag === '[object Node]' || tag === '[object Event]') {
      return undefined;
    }

    // Error 객체는 메시지와 스택만 보존
    if (value instanceof Error) {
      return { name: value.name, message: value.message, stack: value.stack };
    }

    // Date/Map/Set은 JSON 호환 형태로 변환
    if (value instanceof Date) return value.toISOString();
    if (value instanceof Map) return Object.fromEntries(value);
    if (value instanceof Set) return Array.from(value);

    return value;
  };

  try {
    return JSON.parse(JSON.stringify(obj, replacer));
  } catch (_jsonErr) {
    // 3) 최후의 폴백: 아주 단순한 재귀 복사 (Map/Set/Date 처리 포함)
    const simpleClone = (input) => {
      if (input === null || typeof input !== 'object') return input;
      if (input instanceof Date) return new Date(input.getTime());
      if (Array.isArray(input)) return input.map(simpleClone);
      if (input instanceof Map) return new Map(Array.from(input.entries()).map(([k, v]) => [k, simpleClone(v)]));
      if (input instanceof Set) return new Set(Array.from(input.values()).map(simpleClone));

      const out = {};
      for (const k of Object.keys(input)) {
        const v = input[k];
        if (typeof v === 'function' || typeof v === 'symbol') continue;
        const vTag = Object.prototype.toString.call(v);
        if (vTag === '[object Window]' || vTag === '[object Document]' || vTag.startsWith('[object HTML') || vTag === '[object Node]' || vTag === '[object Event]') {
          continue;
        }
        out[k] = simpleClone(v);
      }
      return out;
    };
    return simpleClone(obj);
  }
};

/**
 * reactive 객체를 스냅샷으로 재설정
 * @param {object} reactiveObj - vue reactive 객체
 * @param {object} snapshot - 초기 스냅샷
 */
export const resetReactiveToSnapshot = (reactiveObj, snapshot) => {
  Object.keys(reactiveObj).forEach((k) => {
    // 키가 snapshot에 없으면 undefined 할당
    reactiveObj[k] = snapshot[k];
  });
};

/**
 * ref를 스냅샷으로 재설정
 * @param {import('vue').Ref} refObj - vue ref 객체
 * @param {any} snapshot - 스냅샷 값
 */
export const resetRefToSnapshot = (refObj, snapshot) => {
  refObj.value = snapshot;
};
