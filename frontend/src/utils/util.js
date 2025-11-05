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
 * 객체를 깊은 복사하여 새 인스턴스로 반환
 * @param {any} obj
 * @returns {any}
 */
export const deepClone = (obj) => JSON.parse(JSON.stringify(obj));

/**
 * ref 객체를 스냅샷 값으로 리셋
 * @param {{ value: any }} refObj - Vue ref 객체
 * @param {any} snapshot - 초기 스냅샷 값
 */
export const resetRefToSnapshot = (refObj, snapshot) => {
  refObj.value = deepClone(snapshot);
};

/**
 * reactive 객체를 스냅샷 값으로 리셋
 * @param {object} reactiveObj - Vue reactive 객체
 * @param {any} snapshot - 초기 스냅샷 값
 */
export const resetReactiveToSnapshot = (reactiveObj, snapshot) => {
  const cloned = deepClone(snapshot);
  for (const key of Object.keys(cloned)) {
    reactiveObj[key] = cloned[key];
  }
};