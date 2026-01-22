import React from "react";

/* ---------- Logo ---------- */
export function LogoMark({ className = "" }) {
  return (
    <svg
      className={className}
      width="20"
      height="20"
      viewBox="0 0 24 24"
      fill="none"
      aria-hidden="true"
    >
      <path
        d="M12 2l4 4-4 4-4-4 4-4Z"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinejoin="round"
      />
      <path
        d="M6 12l6 10 6-10"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinejoin="round"
      />
    </svg>
  );
}

/* ---------- Arrow ---------- */
export function ArrowRight({ className = "" }) {
  return (
    <svg
      className={className}
      width="16"
      height="16"
      viewBox="0 0 24 24"
      fill="none"
      aria-hidden="true"
    >
      <path
        d="M5 12h12M13 6l6 6-6 6"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

/* ---------- Social Icons ---------- */
export function GoogleIcon() {
  return (
    <svg viewBox="0 0 24 24" className="h-5 w-5">
      <path
        fill="#EA4335"
        d="M12 10.2v3.6h5.1c-.2 1.3-1.6 3.8-5.1 3.8-3.1 0-5.6-2.6-5.6-5.6S8.9 6.4 12 6.4c1.8 0 3 .8 3.7 1.4l2.5-2.5C16.6 3.8 14.5 2.8 12 2.8 6.9 2.8 2.8 6.9 2.8 12S6.9 21.2 12 21.2c6.9 0 8.6-4.9 8.6-7.4 0-.5-.1-.9-.1-1.3H12Z"
      />
    </svg>
  );
}

export function FacebookIcon() {
  return (
    <svg viewBox="0 0 24 24" className="h-5 w-5 fill-[#1877F2]">
      <path d="M22 12a10 10 0 1 0-11.6 9.9v-7h-2.7v-2.9h2.7V9.7c0-2.7 1.6-4.2 4.1-4.2 1.2 0 2.5.2 2.5.2v2.7h-1.4c-1.4 0-1.8.9-1.8 1.7v2h3.1l-.5 2.9h-2.6v7A10 10 0 0 0 22 12Z" />
    </svg>
  );
}
