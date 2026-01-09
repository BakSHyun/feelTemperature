-- Participant에 user_id 추가
-- 회원과 매칭 참여를 연결하기 위한 필드

-- user_id 컬럼 추가 (nullable - 기존 데이터와 호환)
ALTER TABLE participants ADD COLUMN user_id BIGINT;

-- 외래키 제약 조건 추가
ALTER TABLE participants 
ADD CONSTRAINT fk_participants_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL;

-- 인덱스 추가
CREATE INDEX idx_participants_user_id ON participants(user_id);

