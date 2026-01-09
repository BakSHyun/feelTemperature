-- V1__Create_initial_schema.sql
-- Relationship Status Tracker Database Schema
-- PostgreSQL

-- Matchings Table
CREATE TABLE matchings (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    qr_code_path VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    CONSTRAINT uk_matchings_code UNIQUE (code)
);

CREATE INDEX idx_matchings_code ON matchings(code);

-- Participants Table
CREATE TABLE participants (
    id BIGSERIAL PRIMARY KEY,
    matching_id BIGINT NOT NULL,
    participant_code VARCHAR(36) NOT NULL UNIQUE,
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_participants_matching FOREIGN KEY (matching_id) REFERENCES matchings(id) ON DELETE CASCADE,
    CONSTRAINT uk_participants_code UNIQUE (participant_code)
);

CREATE INDEX idx_participants_matching_id ON participants(matching_id);
CREATE INDEX idx_participants_code ON participants(participant_code);

-- Questions Table
CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    question_text TEXT NOT NULL,
    question_type VARCHAR(50) NOT NULL,
    "order" INTEGER NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_questions_order ON questions("order");
CREATE INDEX idx_questions_is_active ON questions(is_active);

-- Question Choices Table
CREATE TABLE question_choices (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL,
    choice_text TEXT NOT NULL,
    choice_value VARCHAR(100) NOT NULL,
    "order" INTEGER NOT NULL,
    temperature_weight DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_question_choices_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

CREATE INDEX idx_question_choices_question_id ON question_choices(question_id);
CREATE INDEX idx_question_choices_order ON question_choices("order");

-- Answers Table
CREATE TABLE answers (
    id BIGSERIAL PRIMARY KEY,
    participant_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    choice_id BIGINT NOT NULL,
    answered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_answers_participant FOREIGN KEY (participant_id) REFERENCES participants(id) ON DELETE CASCADE,
    CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    CONSTRAINT fk_answers_choice FOREIGN KEY (choice_id) REFERENCES question_choices(id) ON DELETE CASCADE
);

CREATE INDEX idx_answers_participant_id ON answers(participant_id);
CREATE INDEX idx_answers_question_id ON answers(question_id);
CREATE INDEX idx_answers_choice_id ON answers(choice_id);

-- Records Table
CREATE TABLE records (
    id BIGSERIAL PRIMARY KEY,
    record_id VARCHAR(36) NOT NULL UNIQUE,
    matching_id BIGINT NOT NULL UNIQUE,
    temperature DOUBLE PRECISION,
    temperature_diff DOUBLE PRECISION,
    summary JSONB,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_records_matching FOREIGN KEY (matching_id) REFERENCES matchings(id) ON DELETE CASCADE,
    CONSTRAINT uk_records_record_id UNIQUE (record_id),
    CONSTRAINT uk_records_matching_id UNIQUE (matching_id)
);

CREATE INDEX idx_records_record_id ON records(record_id);
CREATE INDEX idx_records_matching_id ON records(matching_id);
CREATE INDEX idx_records_is_active ON records(is_active);

-- Admin Users Table
CREATE TABLE admin_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_admin_users_username UNIQUE (username)
);

CREATE INDEX idx_admin_users_username ON admin_users(username);

