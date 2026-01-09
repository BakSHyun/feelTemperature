# CMS 프론트엔드 개발 규칙

**마지막 업데이트**: 2025-01-04

## 목차

1. [개요](#개요)
2. [코드 품질 원칙](#코드-품질-원칙)
3. [프로젝트 구조](#프로젝트-구조)
4. [컴포넌트 개발 규칙](#컴포넌트-개발-규칙)
5. [상태 관리](#상태-관리)
6. [API 통신](#api-통신)
7. [스타일링](#스타일링)
8. [타입 안전성](#타입-안전성)
9. [성능 최적화](#성능-최적화)
10. [테스트](#테스트)
11. [재사용성 및 모듈화](#재사용성-및-모듈화)
12. [에러 처리](#에러-처리)
13. [접근성 (Accessibility)](#접근성-accessibility)
14. [보안](#보안)
15. [코드 리뷰 체크리스트](#코드-리뷰-체크리스트)

---

## 개요

이 문서는 CMS 프론트엔드 개발 시 반드시 준수해야 할 규칙과 가이드라인을 정의합니다.

### 핵심 원칙

1. **테스트 가능성**: 모든 코드는 테스트하기 쉬워야 함
2. **검증**: 타입 검증, 입력 검증, 에러 검증 필수
3. **성능 최적화**: 렌더링 최소화, 메모리 누수 방지
4. **확장성**: 미래 변경에 대비한 유연한 구조
5. **재활용성**: 중복 코드 제거, 재사용 가능한 컴포넌트
6. **유지보수 용이**: 명확한 네이밍, 문서화, 단순한 구조
7. **중복 금지**: DRY 원칙 준수

---

## 코드 품질 원칙

### 1. DRY (Don't Repeat Yourself)

**❌ 나쁜 예:**
```tsx
function UserList() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  
  useEffect(() => {
    setLoading(true);
    axios.get('/api/users').then(res => {
      setUsers(res.data);
      setLoading(false);
    });
  }, []);
  
  return <div>...</div>;
}

function ProductList() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  
  useEffect(() => {
    setLoading(true);
    axios.get('/api/products').then(res => {
      setProducts(res.data);
      setLoading(false);
    });
  }, []);
  
  return <div>...</div>;
}
```

**✅ 좋은 예:**
```tsx
// hooks/useApi.ts
function useApi<T>(url: string) {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);
  
  useEffect(() => {
    setLoading(true);
    axios.get(url)
      .then(res => setData(res.data))
      .catch(err => setError(err))
      .finally(() => setLoading(false));
  }, [url]);
  
  return { data, loading, error };
}

// 사용
function UserList() {
  const { data: users, loading } = useApi<User[]>('/api/users');
  return <div>...</div>;
}
```

### 2. 단일 책임 원칙 (SRP)

각 컴포넌트는 하나의 책임만 가져야 합니다.

**❌ 나쁜 예:**
```tsx
function QuestionManager() {
  // 질문 조회, 생성, 수정, 삭제, 검색, 필터링 모두 포함
  // 500줄 이상의 코드...
}
```

**✅ 좋은 예:**
```tsx
// components/QuestionList.tsx - 목록만 표시
function QuestionList({ questions, onSelect }: Props) { ... }

// components/QuestionForm.tsx - 폼만 처리
function QuestionForm({ question, onSubmit }: Props) { ... }

// pages/QuestionsPage.tsx - 조율만 담당
function QuestionsPage() {
  const [selected, setSelected] = useState(null);
  return (
    <>
      <QuestionList onSelect={setSelected} />
      <QuestionForm question={selected} />
    </>
  );
}
```

### 3. 명확한 네이밍

- 컴포넌트: PascalCase, 명사형 (예: `QuestionList`, `RecordCard`)
- 함수: camelCase, 동사형 (예: `fetchQuestions`, `handleSubmit`)
- 상수: UPPER_SNAKE_CASE (예: `API_BASE_URL`, `MAX_RETRY_COUNT`)
- 타입/인터페이스: PascalCase, 접미사 `Type` 또는 생략 (예: `Question`, `ApiResponse`)

---

## 프로젝트 구조

```
frontend/
├── src/
│   ├── components/          # 재사용 가능한 컴포넌트
│   │   ├── common/         # 공통 컴포넌트 (Button, Input, Modal 등)
│   │   ├── layout/         # 레이아웃 컴포넌트 (Header, Sidebar 등)
│   │   └── features/       # 기능별 컴포넌트 (QuestionList, RecordCard 등)
│   ├── pages/              # 페이지 컴포넌트
│   │   ├── QuestionsPage.tsx
│   │   ├── RecordsPage.tsx
│   │   └── DashboardPage.tsx
│   ├── services/           # API 서비스 레이어
│   │   ├── api.ts          # Axios 인스턴스 설정
│   │   ├── questionService.ts
│   │   └── recordService.ts
│   ├── hooks/              # Custom Hooks
│   │   ├── useApi.ts
│   │   ├── useAuth.ts
│   │   └── usePagination.ts
│   ├── types/              # TypeScript 타입 정의
│   │   ├── question.types.ts
│   │   ├── record.types.ts
│   │   └── api.types.ts
│   ├── utils/              # 유틸리티 함수
│   │   ├── formatters.ts
│   │   ├── validators.ts
│   │   └── constants.ts
│   ├── context/            # React Context
│   │   ├── AuthContext.tsx
│   │   └── ThemeContext.tsx
│   ├── App.tsx
│   └── index.tsx
```

### 디렉토리 규칙

- **components/**: 재사용 가능한 UI 컴포넌트
- **pages/**: 라우트와 매핑되는 페이지 컴포넌트
- **services/**: API 호출 로직 (비즈니스 로직 없음)
- **hooks/**: 상태 관리 및 부수 효과 로직
- **types/**: TypeScript 타입 정의 (재사용 가능한 타입)
- **utils/**: 순수 함수 (테스트 가능)

---

## 컴포넌트 개발 규칙

### 1. 함수형 컴포넌트 사용

**✅ 권장:**
```tsx
interface QuestionCardProps {
  question: Question;
  onEdit: (id: number) => void;
  onDelete: (id: number) => void;
}

export function QuestionCard({ question, onEdit, onDelete }: QuestionCardProps) {
  return (
    <div className="card">
      <h3>{question.text}</h3>
      <button onClick={() => onEdit(question.id)}>수정</button>
      <button onClick={() => onDelete(question.id)}>삭제</button>
    </div>
  );
}
```

### 2. Props 타입 명시

모든 컴포넌트는 명확한 Props 타입을 가져야 합니다.

**❌ 나쁜 예:**
```tsx
function QuestionCard(props: any) { ... }
```

**✅ 좋은 예:**
```tsx
interface QuestionCardProps {
  question: Question;
  onEdit: (id: number) => void;
}

function QuestionCard({ question, onEdit }: QuestionCardProps) { ... }
```

### 3. 컴포넌트 크기 제한

- 단일 컴포넌트는 200줄 이내 권장
- 300줄 초과 시 분리 검토 필수
- 복잡한 로직은 커스텀 훅으로 분리

### 4. 조건부 렌더링

**✅ 명확한 조건부 렌더링:**
```tsx
function QuestionList({ questions, loading, error }: Props) {
  if (error) return <ErrorMessage error={error} />;
  if (loading) return <LoadingSpinner />;
  if (questions.length === 0) return <EmptyState />;
  
  return (
    <ul>
      {questions.map(q => <QuestionItem key={q.id} question={q} />)}
    </ul>
  );
}
```

### 5. Key Props 필수

리스트 렌더링 시 고유한 key 필수:

```tsx
{items.map(item => (
  <ItemCard key={item.id} item={item} />
))}
```

---

## 상태 관리

### 1. 상태 관리 선택 기준

- **로컬 상태 (useState)**: 컴포넌트 내부만 사용
- **Context**: 여러 컴포넌트 공유 (인증, 테마 등)
- **서버 상태**: React Query 또는 SWR 사용 권장
- **전역 상태**: Redux/Zustand (필요 시)

### 2. useState 사용 규칙

**❌ 나쁜 예:**
```tsx
const [state, setState] = useState({
  name: '',
  email: '',
  age: 0,
  loading: false,
  error: null,
});
```

**✅ 좋은 예:**
```tsx
const [name, setName] = useState('');
const [email, setEmail] = useState('');
const [age, setAge] = useState(0);
const [loading, setLoading] = useState(false);
const [error, setError] = useState<string | null>(null);
```

관련 상태는 객체로 묶을 수 있으나, 타입 정의 필수:

```tsx
interface FormState {
  name: string;
  email: string;
  age: number;
}

const [form, setForm] = useState<FormState>({
  name: '',
  email: '',
  age: 0,
});
```

### 3. useEffect 의존성 배열

의존성 배열을 정확히 명시:

```tsx
// ❌ 나쁜 예
useEffect(() => {
  fetchData(id);
}, []); // id가 변경되어도 실행 안 됨

// ✅ 좋은 예
useEffect(() => {
  fetchData(id);
}, [id]);
```

---

## API 통신

### 1. 서비스 레이어 분리

API 호출은 서비스 레이어에만 작성:

**✅ 좋은 예:**
```tsx
// services/questionService.ts
export const questionService = {
  getAll: (): Promise<Question[]> => 
    api.get('/questions').then(res => res.data),
  
  getById: (id: number): Promise<Question> => 
    api.get(`/questions/${id}`).then(res => res.data),
  
  create: (data: CreateQuestionDto): Promise<Question> => 
    api.post('/questions', data).then(res => res.data),
  
  update: (id: number, data: UpdateQuestionDto): Promise<Question> => 
    api.put(`/questions/${id}`, data).then(res => res.data),
  
  delete: (id: number): Promise<void> => 
    api.delete(`/questions/${id}`).then(() => undefined),
};
```

### 2. Axios 인스턴스 설정

```tsx
// services/api.ts
import axios from 'axios';

export const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터 (인증 토큰 추가)
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 응답 인터셉터 (에러 처리)
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // 인증 실패 처리
    }
    return Promise.reject(error);
  }
);
```

### 3. 에러 처리

모든 API 호출은 에러 처리 필요:

```tsx
try {
  const questions = await questionService.getAll();
  setQuestions(questions);
} catch (error) {
  if (axios.isAxiosError(error)) {
    setError(error.response?.data?.message || '오류가 발생했습니다.');
  } else {
    setError('예상치 못한 오류가 발생했습니다.');
  }
}
```

---

## 스타일링

### 1. Tailwind CSS 사용

- 인라인 스타일 금지
- CSS 파일 최소화 (글로벌 스타일만)
- Tailwind 유틸리티 클래스 사용

**✅ 좋은 예:**
```tsx
<button className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark">
  저장
</button>
```

### 2. 단일 키 컬러 사용

사용자 선호사항에 따라 단일 키 컬러 사용:

```tsx
// tailwind.config.js
colors: {
  primary: {
    DEFAULT: '#3b82f6', // blue-500
    dark: '#2563eb',
    light: '#60a5fa',
  },
}
```

### 3. 반응형 디자인

모바일 퍼스트 접근:

```tsx
<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
  {items.map(item => <Card key={item.id} item={item} />)}
</div>
```

---

## 타입 안전성

### 1. 모든 데이터 타입 정의

**✅ 좋은 예:**
```tsx
// types/question.types.ts
export interface Question {
  id: number;
  text: string;
  order: number;
  active: boolean;
  choices: QuestionChoice[];
}

export interface QuestionChoice {
  id: number;
  questionId: number;
  text: string;
  value: number;
  order: number;
}

export interface CreateQuestionDto {
  text: string;
  order: number;
  choices: Omit<QuestionChoice, 'id' | 'questionId'>[];
}
```

### 2. any 타입 금지

- `any` 사용 시 ESLint 경고/에러 설정
- `unknown` 또는 구체적 타입 사용

### 3. 제네릭 활용

재사용 가능한 타입:

```tsx
interface ApiResponse<T> {
  data: T;
  message?: string;
  status: number;
}

function useApi<T>(url: string): ApiResponse<T> { ... }
```

---

## 성능 최적화

### 1. React.memo 사용

불필요한 리렌더링 방지:

```tsx
export const QuestionCard = React.memo(function QuestionCard({ 
  question, 
  onEdit 
}: QuestionCardProps) {
  return <div>...</div>;
});
```

### 2. useMemo, useCallback 사용

비용이 큰 계산 또는 함수 메모이제이션:

```tsx
const sortedQuestions = useMemo(
  () => questions.sort((a, b) => a.order - b.order),
  [questions]
);

const handleEdit = useCallback(
  (id: number) => {
    onEdit(id);
  },
  [onEdit]
);
```

### 3. 코드 스플리팅

라우트별 코드 스플리팅:

```tsx
const QuestionsPage = lazy(() => import('./pages/QuestionsPage'));
const RecordsPage = lazy(() => import('./pages/RecordsPage'));
```

### 4. 이미지 최적화

- WebP 형식 사용
- lazy loading
- 적절한 크기로 리사이즈

---

## 테스트

### 1. 테스트 필수 범위

- 유틸리티 함수: 100% 커버리지
- 커스텀 훅: 주요 로직 테스트
- 컴포넌트: 주요 사용자 플로우 테스트
- 서비스: API 호출 모킹 테스트

### 2. 테스트 작성 규칙

```tsx
// utils/formatters.test.ts
import { formatDate } from './formatters';

describe('formatDate', () => {
  it('날짜를 올바른 형식으로 포맷해야 함', () => {
    const date = new Date('2024-01-01');
    expect(formatDate(date)).toBe('2024-01-01');
  });
});
```

### 3. 테스트 도구

- Jest (기본 제공)
- React Testing Library
- MSW (Mock Service Worker) - API 모킹

---

## 재사용성 및 모듈화

### 1. 커스텀 훅 활용

반복되는 로직은 커스텀 훅으로 추출:

```tsx
// hooks/usePagination.ts
export function usePagination<T>(
  fetchFn: (page: number) => Promise<T[]>,
  pageSize: number = 10
) {
  const [page, setPage] = useState(1);
  const [data, setData] = useState<T[]>([]);
  const [loading, setLoading] = useState(false);
  
  useEffect(() => {
    setLoading(true);
    fetchFn(page)
      .then(setData)
      .finally(() => setLoading(false));
  }, [page, fetchFn]);
  
  return { data, page, setPage, loading };
}
```

### 2. 공통 컴포넌트 라이브러리

자주 사용하는 컴포넌트는 공통 컴포넌트로:

```
components/
  common/
    Button.tsx
    Input.tsx
    Modal.tsx
    Table.tsx
    Card.tsx
```

---

## 에러 처리

### 1. 에러 바운더리

전역 에러 처리:

```tsx
class ErrorBoundary extends React.Component<Props, State> {
  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    // 에러 로깅
    console.error('Error caught:', error, errorInfo);
  }
  
  render() {
    if (this.state.hasError) {
      return <ErrorFallback />;
    }
    return this.props.children;
  }
}
```

### 2. 사용자 친화적 에러 메시지

```tsx
function ErrorMessage({ error }: { error: Error }) {
  const message = error.message || '오류가 발생했습니다.';
  return (
    <div className="error-message">
      {message}
    </div>
  );
}
```

---

## 접근성 (Accessibility)

### 1. 시맨틱 HTML

```tsx
<main>
  <h1>질문 관리</h1>
  <nav>...</nav>
  <section>...</section>
</main>
```

### 2. 키보드 네비게이션

모든 인터랙티브 요소는 키보드로 접근 가능해야 함.

### 3. ARIA 속성

필요 시 ARIA 속성 사용:

```tsx
<button aria-label="질문 삭제" onClick={handleDelete}>
  <TrashIcon />
</button>
```

---

## 보안

### 1. XSS 방지

React는 기본적으로 XSS를 방지하나, `dangerouslySetInnerHTML` 사용 시 주의.

### 2. 인증 토큰 관리

- localStorage 사용 (서버 사이드 렌더링 없음)
- 토큰 만료 처리
- HTTPS 사용 (프로덕션)

### 3. 입력 검증

클라이언트 및 서버 양쪽 모두 검증:

```tsx
function validateQuestion(data: CreateQuestionDto): ValidationError[] {
  const errors: ValidationError[] = [];
  if (!data.text.trim()) {
    errors.push({ field: 'text', message: '질문 내용은 필수입니다.' });
  }
  return errors;
}
```

---

## 주석 규칙

### 1. 주석 작성 원칙

#### ✅ 주석이 필요한 경우

1. **복잡한 비즈니스 로직**
   - 알고리즘이나 복잡한 계산 로직
   - 비즈니스 규칙이나 제약사항

2. **의도 설명이 필요한 경우**
   - "왜" 이렇게 구현했는지 설명
   - 다른 방법을 시도했지만 현재 방법을 선택한 이유

3. **공개 API (함수, 컴포넌트)**
   - 컴포넌트 Props 문서화 (JSDoc)
   - Public 함수의 사용법과 예제

4. **TODOs 및 FIXMEs**
   - 임시 해결책이나 향후 개선 필요사항

#### ❌ 주석이 불필요한 경우

1. **자명한 코드**
   - 코드만 봐도 이해되는 간단한 로직

2. **변수명으로 충분한 경우**
   - 명확한 네이밍으로 의도가 드러나는 경우

### 2. 주석 스타일

#### JSDoc 주석 (컴포넌트, 함수)

**✅ 좋은 예:**
```tsx
/**
 * 질문 목록을 표시하는 컴포넌트
 * 
 * @param questions - 표시할 질문 목록
 * @param onEdit - 질문 수정 시 호출되는 콜백 함수
 * @param onDelete - 질문 삭제 시 호출되는 콜백 함수
 * 
 * @example
 * ```tsx
 * <QuestionList 
 *   questions={questions} 
 *   onEdit={(id) => handleEdit(id)}
 *   onDelete={(id) => handleDelete(id)}
 * />
 * ```
 */
export function QuestionList({ questions, onEdit, onDelete }: QuestionListProps) {
  // ...
}
```

#### 인라인 주석

**✅ 좋은 예:**
```tsx
// 온도 계산: 가중치가 적용된 질문만 계산에 포함
const weightedQuestions = questions.filter(q => q.weight > 0);

// 매칭 코드 중복 체크 (최대 10회 시도)
let attempts = 0;
while (attempts < 10 && codeExists) {
  code = generateCode();
  codeExists = await checkCodeExists(code);
  attempts++;
}
```

**❌ 나쁜 예:**
```tsx
// questions를 필터링
const filtered = questions.filter(q => q.active);

// 변수 설정
const name = question.name;
```

#### 블록 주석 (복잡한 로직 설명)

**✅ 좋은 예:**
```tsx
/**
 * 온도 계산 로직:
 * 1. 가중치가 있는 질문만 선택
 * 2. 각 질문의 점수에 가중치를 곱함
 * 3. 합계를 가중치 합으로 나눔 (정규화)
 * 4. 0-100 범위로 변환
 */
function calculateTemperature(answers: Answer[]): number {
  // ...
}
```

### 3. TODO 및 FIXME 주석

**형식:**
```tsx
// TODO: [작업 내용] - [예상 완료일 또는 담당자]
// FIXME: [문제 내용] - [해결 방법 또는 참조]
// NOTE: [참고사항]
// HACK: [임시 해결책] - [이유]
```

**예제:**
```tsx
// TODO: API 에러 처리를 전역 에러 핸들러로 이동 - 2025-01-10
// FIXME: 메모리 누수 가능성 - cleanup 함수 추가 필요
// NOTE: 백엔드 API 버전이 v2로 업그레이드되면 이 코드 수정 필요
// HACK: 브라우저 호환성을 위해 Array.from() 사용 (IE11 지원)
```

### 4. 주석 작성 규칙

1. **영어 또는 한글 일관성 유지**
   - 프로젝트 전반에 걸쳐 일관된 언어 사용
   - 일반적으로 영어 권장 (국제 협업 고려)

2. **문법과 맞춤법 확인**
   - 주석도 코드의 일부이므로 정확하게 작성

3. **최신 상태 유지**
   - 코드 변경 시 관련 주석도 함께 업데이트

4. **불필요한 주석 제거**
   - 코드가 변경되면서 의미 없어진 주석은 삭제

### 5. 컴포넌트 문서화 예제

```tsx
/**
 * 질문 관리 페이지
 * 
 * 질문 목록 조회, 생성, 수정, 삭제 기능을 제공합니다.
 * 
 * @component
 * @example
 * ```tsx
 * <QuestionsPage />
 * ```
 */
export function QuestionsPage() {
  // 상태 관리
  const [questions, setQuestions] = useState<Question[]>([]);
  const [loading, setLoading] = useState(false);
  
  // 질문 목록 조회
  useEffect(() => {
    fetchQuestions();
  }, []);
  
  /**
   * 질문 목록을 API에서 가져옵니다.
   * 에러 발생 시 사용자에게 알림을 표시합니다.
   */
  async function fetchQuestions() {
    setLoading(true);
    try {
      const data = await questionService.getAll();
      setQuestions(data);
    } catch (error) {
      showError('질문 목록을 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  }
  
  return (
    <div>
      {/* 질문 목록 표시 */}
      {loading ? (
        <LoadingSpinner />
      ) : (
        <QuestionList questions={questions} />
      )}
    </div>
  );
}
```

### 6. 타입 정의 주석

```tsx
/**
 * 질문 엔티티 타입
 * 
 * 백엔드 API와 동일한 구조를 유지합니다.
 * 
 * @see backend/src/main/java/com/rstracker/entity/Question.java
 */
export interface Question {
  /** 질문 고유 ID */
  id: number;
  
  /** 질문 내용 */
  text: string;
  
  /** 질문 순서 (1부터 시작) */
  order: number;
  
  /** 활성화 여부 */
  active: boolean;
  
  /** 질문 선택지 목록 */
  choices: QuestionChoice[];
}
```

### 7. 복잡한 로직 주석 예제

```tsx
/**
 * 온도 계산: 가중 평균 방식
 * 
 * 공식: Σ(답변 점수 × 가중치) / Σ(가중치) × 100
 * 
 * @param answers - 사용자 답변 목록
 * @param questions - 질문 목록 (가중치 포함)
 * @returns 0-100 사이의 온도 값
 */
function calculateTemperature(
  answers: Answer[],
  questions: Question[]
): number {
  // 1단계: 가중치가 있는 질문만 필터링
  const weightedQuestions = questions.filter(q => q.weight > 0);
  
  // 2단계: 각 답변의 점수 × 가중치 계산
  let totalWeightedScore = 0;
  let totalWeight = 0;
  
  for (const answer of answers) {
    const question = weightedQuestions.find(q => q.id === answer.questionId);
    if (!question) continue;
    
    const choice = question.choices.find(c => c.id === answer.choiceId);
    if (!choice) continue;
    
    totalWeightedScore += choice.value * question.weight;
    totalWeight += question.weight;
  }
  
  // 3단계: 가중 평균 계산 및 0-100 범위로 정규화
  if (totalWeight === 0) return 0;
  
  const average = totalWeightedScore / totalWeight;
  return Math.round(average * 100);
}
```

---

## 코드 리뷰 체크리스트

코드 리뷰 시 확인 사항:

- [ ] 타입 안전성 (any 사용 없음)
- [ ] 컴포넌트 크기 (200줄 이내, 300줄 초과 시 분리)
- [ ] 재사용성 (중복 코드 없음)
- [ ] 성능 최적화 (불필요한 리렌더링 없음)
- [ ] 에러 처리 (모든 API 호출 에러 처리)
- [ ] 테스트 (유틸리티 함수, 커스텀 훅 테스트)
- [ ] 접근성 (시맨틱 HTML, 키보드 네비게이션)
- [ ] 네이밍 (명확한 이름, 일관된 컨벤션)
- [ ] 주석 (복잡한 로직, 공개 API에 적절한 주석)
- [ ] 문서화 (컴포넌트 Props JSDoc 문서화)
- [ ] TODO/FIXME 주석 정리 (의미 있는 내용만)

---

**이 규칙들은 모든 개발자가 준수해야 하며, 코드 리뷰 시 기준이 됩니다.**

