import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";
import { apiRequest } from "../lib/api";

/**
 * Premium Signup Page – Hishab Nikash
 * ✔ Logo routes back to landing page
 * ✔ 3 animated side slides
 * ✔ Floating analytics visuals
 * ✔ Split form sections
 * ✔ Backend wired
 */

export default function SignupPage() {
  const navigate = useNavigate();

  /* ---------------- form state ---------------- */
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
    dateOfBirth: "",
    timezone: "",
    budgetStartDay: 1,
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  function update(key, value) {
    setForm((f) => ({ ...f, [key]: value }));
  }

  /* ---------------- signup submit ---------------- */
  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSuccess("");

    if (!form.name || !form.email || !form.password) {
      setError("Name, email and password are required.");
      return;
    }

    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setLoading(true);
    try {
      await apiRequest("/api/auth/signup", {
        method: "POST",
        body: {
          name: form.name,
          email: form.email,
          password: form.password,
          confirmPassword: form.confirmPassword,
          dateOfBirth: form.dateOfBirth || null,
          timezone: form.timezone || null,
          budgetStartDay: Number(form.budgetStartDay),
        },
      });

      setSuccess("Account created! Please verify your email.");
      setTimeout(() => {
        navigate("/verify-email-info");
      }, 800);      
    } catch (err) {
      setError(err?.message || "Signup failed. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  /* ---------------- slide logic ---------------- */
  const slides = [
    {
      title: "See your money clearly",
      desc: "Track balance, categories, goals and spending patterns in real time.",
    },
    {
      title: "Never miss a bill",
      desc: "Recurring bills with idempotency and smart alerts.",
    },
    {
      title: "Budget with family",
      desc: "Invite members, assign roles, and manage finances together.",
    },
  ];

  const [slide, setSlide] = useState(0);

  useEffect(() => {
    const id = setInterval(
      () => setSlide((s) => (s + 1) % slides.length),
      4500
    );
    return () => clearInterval(id);
  }, []);

  /* ---------------- render ---------------- */
  return (
    <div className="relative min-h-screen overflow-hidden bg-gradient-to-b from-[#0f3b28] via-[#0b2f21] to-[#062016] text-white">
      {/* ambient glows */}
      <Glow className="-top-40 left-1/2 h-[520px] w-[900px]" />
      <Glow className="bottom-0 -left-40 h-[420px] w-[420px]" />

      <div className="mx-auto flex min-h-screen max-w-6xl items-center justify-center px-4">
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          className="grid w-full overflow-hidden rounded-[32px] bg-white/10 backdrop-blur-xl ring-1 ring-white/15 shadow-[0_40px_120px_rgba(0,0,0,0.45)] md:grid-cols-2"
        >
          {/* ================= LEFT SLIDES ================= */}
          <div className="relative hidden min-h-[720px] p-10 md:block">
            {/* logo → landing */}
            <Link to="/" className="flex items-center gap-2">
              <LogoMark />
              <span className="text-lg font-bold">
                Hishab <span className="text-lime-200">Nikash</span>
              </span>
            </Link>

            {/* floating analytics */}
            <motion.div
              animate={{ y: [0, -12, 0] }}
              transition={{ duration: 6, repeat: Infinity }}
              className="mt-14 rounded-[28px] bg-white/10 p-6 ring-1 ring-white/15"
            >
              <div className="text-sm font-semibold text-white/80">
                Monthly Analytics
              </div>

              <div className="mt-6 grid grid-cols-12 items-end gap-2">
                {Array.from({ length: 12 }).map((_, i) => (
                  <div
                    key={i}
                    className={`rounded-full ${
                      i === 7 ? "bg-lime-300" : "bg-white/15"
                    }`}
                    style={{ height: 24 + ((i * 11) % 80) }}
                  />
                ))}
              </div>
            </motion.div>

            {/* slide text */}
            <AnimatePresence mode="wait">
              <motion.div
                key={slide}
                initial={{ opacity: 0, y: 12 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -12 }}
                transition={{ duration: 0.4 }}
                className="mt-12"
              >
                <h2 className="text-3xl font-extrabold">
                  {slides[slide].title}
                </h2>
                <p className="mt-3 max-w-sm text-sm text-white/70">
                  {slides[slide].desc}
                </p>
              </motion.div>
            </AnimatePresence>

            {/* dots */}
            <div className="mt-6 flex gap-2">
              {slides.map((_, i) => (
                <button
                  key={i}
                  onClick={() => setSlide(i)}
                  className={`h-2 rounded-full transition ${
                    i === slide
                      ? "w-8 bg-lime-300"
                      : "w-2 bg-white/30"
                  }`}
                />
              ))}
            </div>
          </div>

          {/* ================= RIGHT FORM ================= */}
          <div className="p-8 sm:p-10">
            <div className="flex items-center justify-between">
              <Link
                to="/"
                className="text-xs text-white/70 hover:text-white"
              >
                ← Back to landing
              </Link>
            </div>

            <h1 className="mt-8 text-3xl font-extrabold">
              Create your account
            </h1>
            <p className="mt-2 text-sm text-white/70">
              Start budgeting smarter today
            </p>

            <form
              onSubmit={handleSubmit}
              className="mt-8 grid gap-6 md:grid-cols-2"
            >
              {/* section A */}
              <div className="space-y-4">
                <SectionTitle>Account</SectionTitle>
                <Input label="Full name" onChange={(e) => update("name", e.target.value)} />
                <Input label="Email" onChange={(e) => update("email", e.target.value)} />
                <Input type="password" label="Password" onChange={(e) => update("password", e.target.value)} />
                <Input type="password" label="Confirm password" onChange={(e) => update("confirmPassword", e.target.value)} />
              </div>

              {/* section B */}
              <div className="space-y-4">
                <SectionTitle>Preferences</SectionTitle>
                <Input type="date" label="Date of birth" onChange={(e) => update("dateOfBirth", e.target.value)} />
                <Select label="Timezone" onChange={(e) => update("timezone", e.target.value)} />
                <Input type="number" min={1} max={28} label="Budget start day" onChange={(e) => update("budgetStartDay", e.target.value)} />
              </div>

              {/* footer */}
              <div className="md:col-span-2">
                {error && <Alert type="error">{error}</Alert>}
                {success && <Alert type="success">{success}</Alert>}

                <motion.button
                  whileHover={{ scale: 1.03 }}
                  whileTap={{ scale: 0.97 }}
                  disabled={loading}
                  className="w-full rounded-2xl bg-lime-300 px-6 py-3 text-sm font-semibold text-[#061a12] shadow-lg hover:bg-lime-200 disabled:opacity-60"
                >
                  {loading ? "Creating account..." : "Create account"}
                </motion.button>

                <p className="mt-5 text-center text-sm text-white/65">
                  Already have an account?{" "}
                  <Link to="/login" className="text-lime-200">
                    Login here
                  </Link>
                </p>
              </div>
            </form>
          </div>
        </motion.div>
      </div>
    </div>
  );
}

/* ================= helpers ================= */

function Glow({ className }) {
  return (
    <div
      className={`pointer-events-none absolute rounded-full bg-[radial-gradient(circle,rgba(190,242,100,0.35),rgba(6,26,18,0)_70%)] blur-2xl ${className}`}
    />
  );
}

function SectionTitle({ children }) {
  return (
    <div className="text-xs font-semibold uppercase tracking-wide text-lime-200">
      {children}
    </div>
  );
}

function Input({ label, ...props }) {
  return (
    <div>
      <label className="mb-1 block text-xs font-semibold text-white/70">
        {label}
      </label>
      <input
        {...props}
        className="w-full rounded-2xl bg-white/10 px-4 py-3 text-sm text-white ring-1 ring-white/15 focus:ring-lime-200/40 outline-none"
      />
    </div>
  );
}

function Select({ label, ...props }) {
  return (
    <div>
      <label className="mb-1 block text-xs font-semibold text-white/70">
        {label}
      </label>
      <select
        {...props}
        className="w-full rounded-2xl bg-white/10 px-4 py-3 text-sm text-white ring-1 ring-white/15 focus:ring-lime-200/40"
      >
        <option value="">Select timezone</option>
        <option value="Asia/Dhaka">Asia/Dhaka</option>
        <option value="UTC">UTC</option>
        <option value="Europe/London">Europe/London</option>
      </select>
    </div>
  );
}

function Alert({ type, children }) {
  return (
    <div
      className={`mb-4 rounded-xl px-4 py-3 text-sm ring-1 ${
        type === "error"
          ? "bg-red-500/15 text-red-100 ring-red-200/20"
          : "bg-lime-400/15 text-lime-100 ring-lime-200/25"
      }`}
    >
      {children}
    </div>
  );
}

function LogoMark() {
  return (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
      <path d="M12 2l4 4-4 4-4-4 4-4Z" stroke="currentColor" strokeWidth="2" />
      <path d="M6 12l6 10 6-10" stroke="currentColor" strokeWidth="2" />
    </svg>
  );
}
