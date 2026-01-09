/**
 * 버튼 컴포넌트
 * 
 * 재사용 가능한 버튼 컴포넌트입니다.
 */

import { type ButtonHTMLAttributes, forwardRef } from 'react';

/**
 * 버튼 변형 타입
 */
type ButtonVariant = 'primary' | 'secondary' | 'danger';

/**
 * 버튼 크기 타입
 */
type ButtonSize = 'sm' | 'md' | 'lg';

/**
 * 버튼 Props
 */
interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  /** 버튼 변형 */
  variant?: ButtonVariant;
  /** 버튼 크기 */
  size?: ButtonSize;
  /** 로딩 상태 */
  loading?: boolean;
}

/**
 * 버튼 스타일 클래스 매핑
 */
const variantClasses: Record<ButtonVariant, string> = {
  primary:
    'bg-primary text-white hover:bg-primary-dark focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary',
  secondary:
    'bg-white/10 text-white ring-1 ring-inset ring-white/20 hover:bg-white/20',
  danger:
    'bg-red-600 text-white hover:bg-red-700 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-red-600',
};

/**
 * 버튼 크기 클래스 매핑
 */
const sizeClasses: Record<ButtonSize, string> = {
  sm: 'rounded-md px-3 py-1.5 text-sm font-semibold',
  md: 'rounded-md px-4 py-2 text-sm font-semibold',
  lg: 'rounded-md px-6 py-3 text-base font-semibold',
};

/**
 * 버튼 컴포넌트
 */
export const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      variant = 'primary',
      size = 'md',
      loading = false,
      disabled,
      children,
      className = '',
      ...props
    },
    ref
  ) => {
    const baseClasses =
      'inline-flex items-center justify-center transition-colors disabled:opacity-50 disabled:cursor-not-allowed';
    const variantClass = variantClasses[variant];
    const sizeClass = sizeClasses[size];

    return (
      <button
        ref={ref}
        disabled={disabled || loading}
        className={`${baseClasses} ${variantClass} ${sizeClass} ${className}`}
        {...props}
      >
        {loading && (
          <svg
            className="-ml-1 mr-2 h-4 w-4 animate-spin"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle
              className="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              strokeWidth="4"
            />
            <path
              className="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
            />
          </svg>
        )}
        {children}
      </button>
    );
  }
);

Button.displayName = 'Button';

