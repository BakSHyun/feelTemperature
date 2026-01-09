-- 회원 테이블 생성
-- 본인인증 서비스 연동을 고려한 구조
-- 아이디(userid)를 키로 사용, 이메일은 선택사항, 전화번호/이름/성별/생년월일은 필수

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    userid VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255),
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    gender VARCHAR(10) NOT NULL, -- MALE, FEMALE, OTHER
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, SUSPENDED, DELETED
    verification_status VARCHAR(20) NOT NULL DEFAULT 'UNVERIFIED', -- UNVERIFIED, PHONE_VERIFIED, EMAIL_VERIFIED, FULLY_VERIFIED
    phone_verified_at TIMESTAMP,
    email_verified_at TIMESTAMP,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX idx_users_userid ON users(userid);
CREATE INDEX idx_users_email ON users(email) WHERE email IS NOT NULL;
CREATE INDEX idx_users_phone_number ON users(phone_number);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_verification_status ON users(verification_status);
CREATE INDEX idx_users_created_at ON users(created_at);

-- updated_at 자동 업데이트 트리거 함수 (PostgreSQL 12+)
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
