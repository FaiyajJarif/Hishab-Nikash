import React, { useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { apiRequest } from "../lib/api";
import { useAuth } from "../context/AuthContext";
import { motion } from "framer-motion";

/**
 * LoginPage.jsx
 * - TailwindCSS required
 * - framer-motion required
 * - Uses your green/lime theme similar to landing page
 *
 * Hook up real auth:
 * Replace handleSubmit() body with your API call and token storage.
 */

export default function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [cooldown, setCooldown] = useState(0);

  const slides = useMemo(
    () => [
      {
        title: "Welcome back!",
        subtitle: "Start managing your finance faster and better",
        footnote: "Track budgets, analytics, recurring bills & goals.",
      },
      {
        title: "Stay in control",
        subtitle: "Real-time alerts and overspend insights",
        footnote: "Built around your Spring Boot backend.",
      },
      {
        title: "Budget with family",
        subtitle: "Invite members, assign roles, and collaborate",
        footnote: "Secure role-based access for shared spending.",
      },
    ],
    []
  );

  const [slideIdx, setSlideIdx] = useState(0);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [rememberMe, setRememberMe] = useState(true);

  const [showPw, setShowPw] = useState(false);
  const [loading, setLoading] = useState(false);

  // Optional: show a playful error animation later
  const [error, setError] = useState("");

  function nextSlide() {
    setSlideIdx((s) => (s + 1) % slides.length);
  }

  function prevSlide() {
    setSlideIdx((s) => (s - 1 + slides.length) % slides.length);
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
  
    if (!email || !password) {
      setError("Please enter email and password.");
      return;
    }
  
    setLoading(true);
    try {
      const res = await apiRequest("/api/auth/login", {
        method: "POST",
        body: { email, password, rememberMe },
      });
  
      login(res.token, rememberMe);
      const status = await apiRequest("/api/onboarding/status");

        if (status.completed) {
          navigate("/dashboard");
        } else {
          navigate("/onboarding");
        }
    } catch (err) {
      if (err.code === "EMAIL_NOT_VERIFIED") {
        setError("Your email is not verified. Please check your inbox.");
      } else {
        setError(err.message || "Login failed");
      }
    } finally {
      setLoading(false);
    }
  }  

  async function handleResend() {
    if (!email || cooldown > 0) return;
  
    try {
      await apiRequest("/api/auth/resend-verification", {
        method: "POST",
        body: { email },
      });
  
      setError("Verification email sent. Please check your inbox.");
  
      setCooldown(30);
      const timer = setInterval(() => {
        setCooldown((c) => {
          if (c <= 1) {
            clearInterval(timer);
            return 0;
          }
          return c - 1;
        });
      }, 1000);
    } catch (err) {
      setError(err.message || "Could not resend verification email.");
    }
  }  

  return (
    <div className="min-h-screen bg-gradient-to-b from-[#0f3b28] via-[#0b2f21] to-[#062016] text-white">
      {/* Background glow */}
      <div className="pointer-events-none absolute -top-40 left-1/2 h-[560px] w-[900px] -translate-x-1/2 rounded-full bg-[radial-gradient(circle,rgba(110,255,154,0.40),rgba(6,26,18,0)_62%)] blur-2xl" />
      <div className="pointer-events-none absolute top-24 -left-40 h-[560px] w-[560px] rounded-full bg-[radial-gradient(circle,rgba(190,242,100,0.30),rgba(6,26,18,0)_70%)] blur-2xl" />

      <div className="mx-auto flex min-h-screen max-w-6xl items-center justify-center px-4 py-10">
        <motion.div
          initial={{ opacity: 0, y: 18 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.55, ease: "easeOut" }}
          className="grid w-full overflow-hidden rounded-[28px] ring-1 ring-white/15 bg-white/10 backdrop-blur-xl shadow-[0_30px_80px_rgba(0,0,0,0.35)] md:grid-cols-2"
        >
          {/* LEFT: Promo panel */}
          <div className="relative hidden min-h-[680px] bg-gradient-to-b from-white/10 to-white/5 p-10 md:block">
            {/* brand */}
            <div className="flex items-center gap-2">
              <div className="grid h-10 w-10 place-items-center rounded-xl bg-lime-300/20 ring-1 ring-lime-200/25">
                <LogoMark />
              </div>
              <div className="text-sm font-semibold tracking-wide">
                Hishab <span className="text-lime-200">Nikash</span>
              </div>
            </div>

            {/* floating cards (like sample) */}
            <div className="relative mt-12 h-[380px]">
              <FloatCard className="left-0 top-0">
                <div className="flex items-center gap-3">
                  <div className="grid h-10 w-10 place-items-center rounded-xl bg-lime-300/20 ring-1 ring-lime-200/25">
                    <WalletIcon />
                  </div>
                  <div>
                    <div className="text-[11px] uppercase tracking-wider text-white/60">
                      Current Balance
                    </div>
                    <div className="mt-1 text-2xl font-extrabold text-lime-200">
                      ৳24,359
                    </div>
                  </div>
                </div>
              </FloatCard>

              <FloatCard className="left-32 top-24">
                <div className="flex items-center justify-between">
                  <div className="text-sm font-semibold text-white/85">Food</div>
                  <div className="rounded-full bg-white/10 px-2 py-1 text-xs text-white/70 ring-1 ring-white/10">
                    34%
                  </div>
                </div>
                <div className="mt-4">
                  <Donut />
                </div>
              </FloatCard>

              <FloatCard className="left-6 top-52">
                <div className="flex items-start gap-3">
                  <div className="grid h-10 w-10 place-items-center rounded-xl bg-white/10 ring-1 ring-white/10">
                    <PlusIcon />
                  </div>
                  <div>
                    <div className="text-sm font-semibold text-white/90">
                      New transaction
                    </div>
                    <div className="mt-1 text-xs text-white/60">
                      Add quickly or import later
                    </div>
                  </div>
                </div>

                <motion.button
                  whileHover={{ scale: 1.06 }}
                  whileTap={{ scale: 0.95 }}
                  className="mt-5 inline-flex items-center justify-center gap-2 rounded-full bg-lime-300 px-4 py-2 text-sm font-semibold text-[#061a12] hover:bg-lime-200 transition"
                >
                  Add now <ArrowRight />
                </motion.button>
              </FloatCard>

              {/* soft “grid” lines like product mock */}
              <div className="pointer-events-none absolute inset-0 rounded-[24px] bg-[linear-gradient(to_right,rgba(255,255,255,0.08)_1px,transparent_1px),linear-gradient(to_bottom,rgba(255,255,255,0.08)_1px,transparent_1px)] bg-[size:42px_42px] opacity-20" />
            </div>

            {/* slide text */}
            <motion.div
              key={slideIdx}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.35 }}
              className="mt-10"
            >
              <div className="text-3xl font-extrabold tracking-tight">
                {slides[slideIdx].title}
              </div>
              <div className="mt-3 text-sm text-white/75">
                {slides[slideIdx].subtitle}
              </div>
              <div className="mt-2 text-xs text-white/55">
                {slides[slideIdx].footnote}
              </div>
            </motion.div>

            {/* slider controls */}
            <div className="mt-8 flex items-center justify-between">
              <button
                onClick={prevSlide}
                className="grid h-10 w-10 place-items-center rounded-full bg-white/10 ring-1 ring-white/10 hover:bg-white/15 transition"
              >
                ‹
              </button>

              <div className="flex items-center gap-2">
                {slides.map((_, i) => (
                  <button
                    key={i}
                    onClick={() => setSlideIdx(i)}
                    className={[
                      "h-2 rounded-full transition",
                      i === slideIdx ? "w-8 bg-lime-300" : "w-2 bg-white/25",
                    ].join(" ")}
                  />
                ))}
              </div>

              <button
                onClick={nextSlide}
                className="grid h-10 w-10 place-items-center rounded-full bg-white/10 ring-1 ring-white/10 hover:bg-white/15 transition"
              >
                ›
              </button>
            </div>
          </div>

          {/* RIGHT: Form */}
          <div className="p-8 sm:p-10">
            <div className="flex items-center justify-between">
              <div className="md:hidden flex items-center gap-2">
                <div className="grid h-10 w-10 place-items-center rounded-xl bg-lime-300/20 ring-1 ring-lime-200/25">
                  <LogoMark />
                </div>
                <div className="text-sm font-semibold tracking-wide">
                  Hishab <span className="text-lime-200">Nikash</span>
                </div>
              </div>

              <Link
                to="/"
                className="text-xs text-white/70 hover:text-white transition"
              >
                ← Back to landing
              </Link>
            </div>

            <motion.div
              initial={{ opacity: 0, x: 10 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.05, duration: 0.45 }}
              className="mt-10"
            >
              <h1 className="text-3xl font-extrabold tracking-tight">
                Welcome back!
              </h1>
              <p className="mt-2 text-sm text-white/70">
                Start managing your finance faster and better
              </p>
            </motion.div>

            <form onSubmit={handleSubmit} className="mt-8 space-y-4">
              <motion.div
                initial={{ opacity: 0, y: 8 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.08 }}
              >
                <Label>Email</Label>
                <div className="group relative">
                  <span className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2 text-white/50">
                    <MailIcon />
                  </span>
                  <input
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="you@example.com"
                    className="w-full rounded-2xl bg-white/10 px-12 py-3 text-sm text-white placeholder:text-white/40 ring-1 ring-white/15 outline-none transition focus:bg-white/12 focus:ring-lime-200/35"
                  />
                  <div className="pointer-events-none absolute inset-0 rounded-2xl opacity-0 group-focus-within:opacity-100 transition shadow-[0_0_0_6px_rgba(190,242,100,0.10)]" />
                </div>
              </motion.div>

              <motion.div
                initial={{ opacity: 0, y: 8 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.12 }}
              >
                <Label>Password</Label>
                <div className="group relative">
                  <span className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2 text-white/50">
                    <LockIcon />
                  </span>
                  <input
                    type={showPw ? "text" : "password"}
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="At least 8 characters"
                    className="w-full rounded-2xl bg-white/10 px-12 py-3 pr-12 text-sm text-white placeholder:text-white/40 ring-1 ring-white/15 outline-none transition focus:bg-white/12 focus:ring-lime-200/35"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPw((s) => !s)}
                    className="absolute right-4 top-1/2 -translate-y-1/2 text-white/60 hover:text-white transition"
                  >
                    {showPw ? <EyeOffIcon /> : <EyeIcon />}
                  </button>
                  <div className="pointer-events-none absolute inset-0 rounded-2xl opacity-0 group-focus-within:opacity-100 transition shadow-[0_0_0_6px_rgba(190,242,100,0.10)]" />
                </div>

                <div className="mt-2 flex items-center justify-end">
                  <button
                    type="button"
                    onClick={() => navigate("/forgot-password")}
                    className="text-xs text-lime-200 hover:text-lime-100 transition"
                  >
                    Forgot password?
                  </button>
                </div>
                <label className="mt-3 flex items-center gap-2 text-xs text-white/70">
                  <input
                    type="checkbox"
                    checked={rememberMe}
                    onChange={(e) => setRememberMe(e.target.checked)}
                    className="accent-lime-300"
                  />
                  Remember me
                </label>
                {error?.toLowerCase().includes("verify") && (
                  <button
                    type="button"
                    disabled={cooldown > 0}
                    onClick={handleResend}
                    className="mt-2 text-xs text-lime-200 hover:text-lime-100 transition disabled:opacity-40"
                  >
                    {cooldown > 0
                      ? `Resend available in ${cooldown}s`
                      : "Resend verification email"}
                  </button>
                )}
              </motion.div>

              {/* error */}
              {error ? (
                <motion.div
                  initial={{ opacity: 0, y: 6 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="rounded-2xl bg-red-500/15 px-4 py-3 text-sm text-red-100 ring-1 ring-red-200/20"
                >
                  {error}
                </motion.div>
              ) : null}

              {/* CTA */}
              <motion.button
                type="submit"
                whileHover={{ scale: 1.03 }}
                whileTap={{ scale: 0.97 }}
                transition={{ type: "spring", stiffness: 420, damping: 18 }}
                className="relative mt-2 w-full overflow-hidden rounded-2xl bg-lime-300 px-4 py-3 text-sm font-semibold text-[#061a12] shadow-[0_18px_45px_rgba(190,242,100,0.25)] hover:bg-lime-200 transition disabled:opacity-60"
                disabled={loading}
              >
                {/* shimmer */}
                <span className="pointer-events-none absolute inset-0 opacity-60">
                  <span className="absolute -left-1/2 top-0 h-full w-1/2 rotate-12 bg-white/35 blur-md animate-[shimmer_1.35s_linear_infinite]" />
                </span>
                <span className="relative z-10 flex items-center justify-center gap-2">
                  {loading ? "Logging in..." : "Login"}
                  <ArrowRight />
                </span>
              </motion.button>

              {/* Divider */}
              <div className="my-6 flex items-center gap-4">
                <div className="h-px flex-1 bg-white/15" />
                <div className="text-xs text-white/55">or</div>
                <div className="h-px flex-1 bg-white/15" />
              </div>

              {/* Social row (visual only) */}
              <div className="grid gap-3 sm:grid-cols-2">
                <SocialButton label="Google" />
                <SocialButton label="Facebook" />
              </div>

              <div className="mt-6 text-center text-xs text-white/65">
                Don’t you have an account?{" "}
                <Link to="/signup" className="text-lime-200 hover:text-lime-100">
                  Sign Up
                </Link>
              </div>
            </form>

            <div className="mt-10 text-center text-[11px] text-white/45">
              © {new Date().getFullYear()} ALL RIGHTS RESERVED
            </div>
          </div>
        </motion.div>
      </div>
    </div>
  );
}

/* ---------------- components ---------------- */

function Label({ children }) {
  return <div className="mb-2 text-xs font-semibold text-white/75">{children}</div>;
}

function SocialButton({ label }) {
  return (
    <motion.button
      type="button"
      whileHover={{ y: -2 }}
      whileTap={{ scale: 0.98 }}
      transition={{ type: "spring", stiffness: 320, damping: 18 }}
      className="flex items-center justify-center gap-2 rounded-2xl bg-white/10 px-4 py-3 text-sm text-white ring-1 ring-white/15 hover:bg-white/12 transition"
    >
      <span className="grid h-6 w-6 place-items-center rounded-lg bg-white/10 ring-1 ring-white/10">
        {label === "Google" ? "G" : "f"}
      </span>
      {label}
    </motion.button>
  );
}

function FloatCard({ children, className }) {
  return (
    <motion.div
      className={[
        "absolute w-[260px] rounded-[22px] bg-white/12 p-5 ring-1 ring-white/15 shadow-[0_20px_60px_rgba(0,0,0,0.25)]",
        className,
      ].join(" ")}
      animate={{ y: [0, -10, 0] }}
      transition={{ duration: 5.8, repeat: Infinity, ease: "easeInOut" }}
    >
      {children}
    </motion.div>
  );
}

/* ---------------- icons (simple inline) ---------------- */

function LogoMark() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
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

function ArrowRight() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" aria-hidden="true">
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

function MailIcon() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path
        d="M4 6h16v12H4V6Z"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinejoin="round"
      />
      <path
        d="m4 7 8 6 8-6"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinejoin="round"
      />
    </svg>
  );
}

function LockIcon() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path
        d="M7 11V8a5 5 0 0 1 10 0v3"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
      />
      <path
        d="M6 11h12v10H6V11Z"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinejoin="round"
      />
    </svg>
  );
}

function EyeIcon() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path
        d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12Z"
        stroke="currentColor"
        strokeWidth="2"
      />
      <circle cx="12" cy="12" r="3" stroke="currentColor" strokeWidth="2" />
    </svg>
  );
}

function EyeOffIcon() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path
        d="M3 3l18 18"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
      />
      <path
        d="M2 12s4-7 10-7c2.1 0 4 .8 5.6 2"
        stroke="currentColor"
        strokeWidth="2"
      />
      <path
        d="M22 12s-4 7-10 7c-2.1 0-4-.8-5.6-2"
        stroke="currentColor"
        strokeWidth="2"
      />
    </svg>
  );
}

function WalletIcon() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path
        d="M4 7h16v14H4V7Z"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinejoin="round"
      />
      <path
        d="M4 7V5a2 2 0 0 1 2-2h12"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
      />
      <path
        d="M16 14h4"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
      />
    </svg>
  );
}

function PlusIcon() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path
        d="M12 5v14M5 12h14"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
      />
    </svg>
  );
}

function Donut() {
  // simple donut chart mimic
  return (
    <div className="grid place-items-center">
      <div className="relative h-28 w-28">
        <div className="absolute inset-0 rounded-full bg-white/10 ring-1 ring-white/10" />
        <div className="absolute inset-0 rounded-full [background:conic-gradient(#bef264_0_34%,rgba(255,255,255,0.12)_34%_100%)]" />
        <div className="absolute inset-3 rounded-full bg-[#0b2f21] ring-1 ring-white/10" />
        <div className="absolute inset-0 grid place-items-center text-center">
          <div className="text-xl font-extrabold text-white">34%</div>
          <div className="text-xs text-white/70">Food</div>
        </div>
      </div>
    </div>
  );
}
