#!/bin/bash

# 로컬 PostgreSQL 데이터베이스 초기화 스크립트

DB_NAME="relationship_tracker"
DB_USER=${DB_USERNAME:-$(whoami)}

echo "🔧 로컬 PostgreSQL 데이터베이스 초기화"
echo "========================================"
echo "데이터베이스: $DB_NAME"
echo "사용자: $DB_USER"
echo ""

# 스키마 파일 경로
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SCHEMA_FILE="$SCRIPT_DIR/schema.sql"

if [ ! -f "$SCHEMA_FILE" ]; then
    echo "❌ schema.sql 파일을 찾을 수 없습니다: $SCHEMA_FILE"
    exit 1
fi

echo "📄 스키마 파일 실행 중..."
psql -d "$DB_NAME" -f "$SCHEMA_FILE"

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ 데이터베이스 초기화 완료!"
    echo ""
    echo "테이블 목록:"
    psql -d "$DB_NAME" -c "\dt"
else
    echo ""
    echo "❌ 데이터베이스 초기화 실패"
    exit 1
fi

