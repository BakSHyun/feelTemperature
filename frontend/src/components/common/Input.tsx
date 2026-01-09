/**
 * 입력 필드 컴포넌트
 * 
 * 재사용 가능한 입력 필드 컴포넌트입니다.
 */

import { type InputHTMLAttributes, forwardRef } from 'react';

/**
 * 입력 필드 Props
 */
interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  /** 레이블 */
  label?: string;
  /** 에러 메시지 */
  error?: string;
  /** 필수 여부 */
  required?: boolean;
}

/**
 * 입력 필드 컴포넌트
 */
export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, required, className = '', ...props }, ref) => {
    const baseClasses =
      'block w-full rounded-md bg-white/5 px-3 py-2 text-white ring-1 ring-inset ring-white/10 placeholder:text-gray-500 focus:ring-2 focus:ring-inset focus:ring-primary disabled:cursor-not-allowed disabled:bg-gray-500/20 disabled:text-gray-400';
    const errorClasses = error ? 'ring-red-500/50 focus:ring-red-500' : '';

    return (
      <div className="w-full">
        {label && (
          <label className="mb-1 block text-sm font-medium text-gray-300">
            {label}
            {required && <span className="ml-1 text-red-400">*</span>}
          </label>
        )}
        <input
          ref={ref}
          className={`${baseClasses} ${errorClasses} ${className}`}
          {...props}
        />
        {error && <p className="mt-1 text-sm text-red-400">{error}</p>}
      </div>
    );
  }
);

Input.displayName = 'Input';

