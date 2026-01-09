#!/bin/bash

# Supabase 데이터베이스 초기화 스크립트

echo "🔧 Supabase 데이터베이스 초기화"
echo "================================"
echo ""

# .env 파일에서 환경 변수 로드
if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
fi

# Supabase 연결 정보 확인
if [ -z "$SUPABASE_HOST" ] || [ -z "$SUPABASE_DB_PASSWORD" ]; then
    echo "❌ 환경 변수가 설정되지 않았습니다"
    echo ""
    echo ".env 파일에 다음을 설정하세요:"
    echo "SUPABASE_HOST=db.xxxxx.supabase.co"
    echo "SUPABASE_DB_PASSWORD=your-password"
    exit 1
fi

echo "호스트: $SUPABASE_HOST"
echo ""

# 스키마 파일 경로
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SCHEMA_FILE="$SCRIPT_DIR/schema.sql"

if [ ! -f "$SCHEMA_FILE" ]; then
    echo "❌ schema.sql 파일을 찾을 수 없습니다: $SCHEMA_FILE"
    exit 1
fi

# 연결 문자열 구성
CONNECTION_STRING="postgresql://postgres:${SUPABASE_DB_PASSWORD}@${SUPABASE_HOST}:5432/postgres?sslmode=require"

echo "📄 스키마 파일 실행 중..."
echo "⚠️  주의: 기존 테이블이 있다면 삭제됩니다!"
echo ""

# 비대화형 모드 확인 (환경 변수 또는 인자로)
if [ "$1" = "-y" ] || [ "$1" = "--yes" ] || [ "$SKIP_CONFIRM" = "true" ]; then
    echo "비대화형 모드로 진행합니다..."
else
    read -p "계속하시겠습니까? (y/N): " -n 1 -r
    echo ""
    
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "취소되었습니다."
        exit 1
    fi
fi

psql "$CONNECTION_STRING" -f "$SCHEMA_FILE"

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Supabase 데이터베이스 초기화 완료!"
    echo ""
    echo "테이블 목록:"
    psql "$CONNECTION_STRING" -c "\dt"
else
    echo ""
    echo "❌ 데이터베이스 초기화 실패"
    exit 1
fi

