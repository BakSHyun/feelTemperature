-- 질문 카테고리 필드 추가
-- 질문 사용 조건을 구분하기 위한 필드
-- INITIAL_MATCHING: 처음 매칭되었을 때 사용되는 질문
-- TEMPERATURE_REFINE: 온도 맞춰보기 기능에서 사용되는 질문

ALTER TABLE questions ADD COLUMN question_category VARCHAR(50) DEFAULT 'INITIAL_MATCHING';

-- 기본값 설정 (기존 데이터는 INITIAL_MATCHING으로 설정)
UPDATE questions SET question_category = 'INITIAL_MATCHING' WHERE question_category IS NULL;

-- NOT NULL 제약 조건 추가
ALTER TABLE questions ALTER COLUMN question_category SET NOT NULL;

-- 인덱스 추가
CREATE INDEX idx_questions_category ON questions(question_category);
CREATE INDEX idx_questions_category_active ON questions(question_category, is_active);

