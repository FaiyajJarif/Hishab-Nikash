import React, { useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { motion } from "framer-motion";

/**
 * LandingPage.jsx (FULL)
 * ✅ Fixes initial “blank until scroll” by NOT using whileInView on Hero.
 * ✅ Brighter green background + stronger glow.
 * ✅ Flow button animations (spring + shimmer + glow).
 * ✅ Removes direct /dashboard navigation from landing page.
 *
 * Requirements:
 * 1) npm i framer-motion
 * 2) Tailwind enabled
 * 3) Add shimmer keyframes in src/index.css:
 *
 *   @keyframes shimmer {
 *     0% { transform: translateX(-120%) rotate(12deg); }
 *     100% { transform: translateX(220%) rotate(12deg); }
 *   }
 */

export default function LandingPage() {
  const navigate = useNavigate();
  const [pricingMode, setPricingMode] = useState("ANNUAL"); // ANNUAL | MONTHLY
  const [openFaq, setOpenFaq] = useState(2);

  const pricing = useMemo(() => {
    const annual = {
      basic: { price: "৳100", suffix: "/Annually" },
      business: { price: "৳200", suffix: "/Annually" },
      pro: { price: "৳300", suffix: "/Annually" },
      badge: "-10%",
    };
    const monthly = {
      basic: { price: "৳12", suffix: "/Monthly" },
      business: { price: "৳20", suffix: "/Monthly" },
      pro: { price: "৳30", suffix: "/Monthly" },
      badge: null,
    };
    return pricingMode === "ANNUAL" ? annual : monthly;
  }, [pricingMode]);

  const navItems = [
    { label: "Home", href: "#home" },
    { label: "Features", href: "#features" },
    { label: "Pricing", href: "#pricing" },
    { label: "FAQ", href: "#faq" },
    { label: "Contact", href: "#contact" },
  ];

  const featureTiles = [
    {
      title: "Balance Check",
      desc: "See your balance, assigned, and available at a glance—fast like YNAB style.",
      accent: "lime",
    },
    {
      title: "Data Analytics",
      desc: "Daily & monthly summaries, category insights, rolling averages, and alerts.",
      accent: "green",
    },
    {
      title: "Recurring Bills",
      desc: "Idempotent scheduling + processed-once protection to prevent duplicates.",
      accent: "lime",
    },
    {
      title: "Family Budgeting",
      desc: "Create families, invite members, assign roles, and budget together securely.",
      accent: "green",
    },
    {
      title: "Goals & Savings",
      desc: "Create goals and allocate leftover money after month close to stay on track.",
      accent: "lime",
    },
  ];

  const faq = [
    {
      q: "What is Hishab Nikash?",
      a: "A modern budgeting app: categories, monthly budgets, daily analytics, recurring bills, goals, and optional family sharing—built for real-time updates.",
    },
    {
      q: "Does it support daily and monthly analytics?",
      a: "Yes. Daily summary, category breakdown, heatmaps, monthly summary/trends, insights, and overspend alerts are exposed via your analytics endpoints.",
    },
    {
      q: "Can I create a family and invite members?",
      a: "Yes. Family creation, invites, accept/reject tokens, roles, members listing, remove/leave, and family budget endpoints are in place.",
    },
    {
      q: "How is recurring bill duplication prevented?",
      a: "DB-level uniqueness on (bill_id, due_date) + idempotency keys for create/toggle/delete protects against retries and double-processing.",
    },
    {
      q: "Is my data private?",
      a: "Endpoints are scoped by user and family authorization checks enforce role-based permissions for shared resources.",
    },
  ];

  const testimonials = [
    {
      name: "Chloe Hartley",
      title: "Agency CEO",
      quote:
        "This app gives me peace of mind every day. Simple, smart, and built for real users.",
      stars: 5,
    },
    {
      name: "Ava Michell",
      title: "Project Director",
      quote:
        "Budgeting feels satisfying now. The analytics and insights are genuinely useful.",
      stars: 5,
    },
    {
      name: "Ella Monroe",
      title: "Project Coordinator",
      quote:
        "The category system and alerts helped me stay consistent without stress.",
      stars: 5,
    },
    {
      name: "Edward Collins",
      title: "Frontend Engineer",
      quote:
        "Clean UX, great structure. Family collaboration is a huge win for shared spending.",
      stars: 5,
    },
  ];

  return (
    <div className="relative min-h-screen overflow-x-hidden text-white bg-gradient-to-b from-[#0f3b28] via-[#0b2f21] to-[#062016]">
      {/* Background glows (brighter) */}
      <div className="pointer-events-none absolute -top-40 left-1/2 h-[560px] w-[900px] -translate-x-1/2 rounded-full bg-[radial-gradient(circle,rgba(110,255,154,0.42),rgba(6,26,18,0)_62%)] blur-2xl" />
      <div className="pointer-events-none absolute top-24 -left-40 h-[560px] w-[560px] rounded-full bg-[radial-gradient(circle,rgba(190,242,100,0.34),rgba(6,26,18,0)_70%)] blur-2xl" />
      <div className="pointer-events-none absolute bottom-0 right-0 h-[620px] w-[620px] rounded-full bg-[radial-gradient(circle,rgba(52,211,153,0.28),rgba(6,26,18,0)_70%)] blur-2xl" />

      {/* NAV */}
      <header className="sticky top-0 z-50 backdrop-blur supports-[backdrop-filter]:bg-black/10">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-4">
          <button
            onClick={() => scrollToId("home")}
            className="flex items-center gap-2 font-semibold tracking-tight"
          >
            <span className="grid h-9 w-9 place-items-center rounded-xl bg-lime-300/20 ring-1 ring-lime-200/25">
              <LogoMark />
            </span>
            <span className="text-lg">
              Hishab <span className="text-lime-200">Nikash</span>
            </span>
          </button>

          <nav className="hidden items-center gap-6 text-sm text-white/75 md:flex">
            {navItems.map((n) => (
              <button
                key={n.href}
                onClick={() => scrollToId(n.href.replace("#", ""))}
                className="hover:text-white transition"
              >
                {n.label}
              </button>
            ))}
          </nav>

          <div className="flex items-center gap-3">
            <Link
              to="/login"
              className="hidden rounded-full px-4 py-2 text-sm text-white/80 hover:text-white md:inline-flex"
            >
              Login
            </Link>
            <Link
              to="/signup"
              className="rounded-full bg-lime-300 px-5 py-2.5 text-sm font-semibold text-[#061a12] shadow-[0_10px_30px_rgba(190,242,100,0.28)] hover:bg-lime-200 transition"
            >
              Get Started
            </Link>
          </div>
        </div>
      </header>

      {/* HERO (IMPORTANT: animate immediately, NOT whileInView) */}
      <motion.section
        id="home"
        initial="hidden"
        animate="visible"
        variants={stagger}
        className="relative"
      >
        <div className="mx-auto max-w-6xl px-4 pt-14 pb-14 md:pt-20">
          <div className="grid items-center gap-10 md:grid-cols-2">
            <div>
              <motion.div variants={fadeUp} className="inline-flex items-center gap-2 rounded-full bg-white/10 px-4 py-2 text-xs text-white/80 ring-1 ring-white/15">
                <span className="h-2 w-2 rounded-full bg-lime-300" />
                Unlock your financial potential
              </motion.div>

              <motion.h1
                variants={fadeUp}
                className="mt-5 text-4xl font-extrabold leading-tight tracking-tight md:text-5xl"
              >
                Get Your Finances{" "}
                <span className="text-lime-200">Under Control</span>
                <br className="hidden md:block" /> In Minutes
              </motion.h1>

              <motion.p variants={fadeUp} className="mt-4 max-w-xl text-sm leading-6 text-white/75 md:text-base">
                Smart budgeting, daily insights, recurring bills, goals, and family
                collaboration—powered by your backend.
              </motion.p>

              <motion.div variants={fadeUp} className="mt-7 flex flex-wrap items-center gap-3">
                <GlowButton variant="primary" onClick={() => navigate("/signup")}>
                  Get Started
                </GlowButton>

                <GlowButton variant="secondary" onClick={() => scrollToId("features")}>
                  Learn More
                </GlowButton>

                {/* ✅ No direct dashboard access from landing */}
                <GlowButton variant="secondary" onClick={() => navigate("/login")}>
                  Login to Dashboard
                </GlowButton>
              </motion.div>

              <motion.div variants={fadeUp} className="mt-8 grid max-w-xl grid-cols-3 gap-4">
                <StatCard label="Realtime Alerts" value="WebSocket" />
                <StatCard label="Analytics" value="Daily/Monthly" />
                <StatCard label="Family" value="Roles" />
              </motion.div>
            </div>

            {/* Hero mock device + floating cards */}
            <motion.div variants={fadeUp} className="relative">
              <div className="absolute -inset-6 -z-10 rounded-[32px] bg-[radial-gradient(circle_at_20%_20%,rgba(190,242,100,0.34),rgba(255,255,255,0.0)_55%)]" />
              <div className="rounded-[32px] bg-white/10 p-5 ring-1 ring-white/15">
                <div className="relative overflow-hidden rounded-[26px] bg-gradient-to-b from-white/15 to-white/5 p-6">
                  <div className="flex items-center justify-between">
                    <div className="text-sm font-semibold text-white/90">
                      Investment Statistic
                    </div>
                    <div className="rounded-full bg-white/15 px-3 py-1 text-xs text-white/80 ring-1 ring-white/10">
                      Year
                    </div>
                  </div>

                  <div className="mt-5 grid grid-cols-3 gap-2 rounded-2xl bg-black/20 p-2 ring-1 ring-white/10">
                    <Pill active={false} text="Week" />
                    <Pill active={false} text="Month" />
                    <Pill active={true} text="Year" />
                  </div>

                  <div className="mt-6 grid grid-cols-12 items-end gap-2">
                    {Array.from({ length: 12 }).map((_, i) => (
                      <div
                        key={i}
                        className="rounded-full bg-white/12 ring-1 ring-white/10"
                        style={{ height: 24 + ((i * 13) % 80) }}
                      />
                    ))}
                  </div>

                  {/* floating mini cards (animated) */}
                  <motion.div
                    className="absolute right-6 top-20 w-52 rounded-2xl bg-white/95 p-4 text-[#061a12] shadow-xl"
                    animate={{ y: [0, -10, 0] }}
                    transition={{ duration: 5.5, repeat: Infinity, ease: "easeInOut" }}
                  >
                    <div className="text-xs text-black/60">Main Balance</div>
                    <div className="mt-1 text-lg font-extrabold">৳73,800</div>
                    <div className="mt-3 h-2 w-full rounded-full bg-black/10">
                      <div className="h-2 w-[72%] rounded-full bg-lime-400" />
                    </div>
                    <div className="mt-2 text-[11px] text-black/50">Updated today</div>
                  </motion.div>

                  <motion.div
                    className="absolute left-6 bottom-6 w-56 rounded-2xl bg-white/95 p-4 text-[#061a12] shadow-xl"
                    animate={{ y: [0, 8, 0] }}
                    transition={{ duration: 6, repeat: Infinity, ease: "easeInOut" }}
                  >
                    <div className="text-xs text-black/60">Quick Transaction</div>
                    <div className="mt-2 flex items-center gap-2">
                      <Avatar initials="HN" />
                      <Avatar initials="FA" />
                      <Avatar initials="RM" />
                      <motion.button
                        whileHover={{ scale: 1.12 }}
                        whileTap={{ scale: 0.92 }}
                        className="ml-auto grid h-9 w-9 place-items-center rounded-full bg-lime-400 text-black"
                      >
                        +
                      </motion.button>
                    </div>
                  </motion.div>
                </div>
              </div>

              <p className="mt-4 text-center text-xs text-white/60">
                (UI mock) — we’ll connect real data once your dashboard pages are ready.
              </p>
            </motion.div>
          </div>
        </div>
      </motion.section>

      {/* TRUSTED BY */}
      <motion.section
        className="bg-[#072519]"
        initial="hidden"
        whileInView="visible"
        viewport={{ once: true, amount: 0.2 }}
        variants={stagger}
      >
        <div className="mx-auto max-w-6xl px-4 py-14">
          <motion.div variants={fadeUp} className="text-center text-sm text-white/70">
            Trusted By Innovative Companies
          </motion.div>

          <motion.div variants={fadeUp} className="mt-7 grid grid-cols-2 gap-6 opacity-85 sm:grid-cols-3 md:grid-cols-5">
            {["Logoipsum", "Logoipsum", "logoipsum", "logoipsum", "Logoipsum"].map((t, i) => (
              <motion.div
                key={i}
                whileHover={{ y: -4 }}
                transition={{ type: "spring", stiffness: 260, damping: 18 }}
                className="flex items-center justify-center gap-2 rounded-2xl bg-white/10 py-4 ring-1 ring-white/15"
              >
                <div className="h-8 w-8 rounded-xl bg-white/12" />
                <span className="text-sm font-semibold tracking-tight">{t}</span>
              </motion.div>
            ))}
          </motion.div>
        </div>
      </motion.section>

      {/* FEATURES */}
      <motion.section
        id="features"
        className="bg-[#083023]"
        initial="hidden"
        whileInView="visible"
        viewport={{ once: true, amount: 0.18 }}
        variants={stagger}
      >
        <div className="mx-auto max-w-6xl px-4 py-16">
          <motion.h2 variants={fadeUp} className="text-center text-3xl font-extrabold tracking-tight md:text-4xl">
            Here Are Some Of Our <span className="text-lime-200">Cool Features</span>
          </motion.h2>

          <motion.p variants={fadeUp} className="mx-auto mt-4 max-w-2xl text-center text-sm text-white/75 md:text-base">
            Smart finance features designed to put you in control—budgeting, analytics, alerts, and family sharing.
          </motion.p>

          <motion.div variants={fadeUp} className="mt-10 grid gap-6 md:grid-cols-2">
            {/* Left feature */}
            <motion.div whileHover={{ y: -4 }} className="rounded-[28px] bg-white/10 p-6 ring-1 ring-white/15">
              <div className="text-xl font-bold">Balance Check</div>
              <p className="mt-2 text-sm text-white/70">
                Track income, assigned, remaining, and per-category available—fast and clear.
              </p>

              <div className="mt-6 rounded-3xl bg-black/20 p-6 ring-1 ring-white/10">
                <div className="flex items-end justify-between">
                  <div>
                    <div className="text-xs text-white/65">Total Balance</div>
                    <div className="mt-1 text-3xl font-extrabold text-lime-200">
                      ৳16,531
                    </div>
                  </div>
                  <div className="rounded-full bg-white/15 px-3 py-1 text-xs text-white/80 ring-1 ring-white/10">
                    +5.2%
                  </div>
                </div>

                <div className="mt-6 flex items-center justify-center">
                  <Gauge />
                </div>

                <div className="mt-6 grid grid-cols-5 gap-2">
                  {["📊", "💳", "➕", "🎯", "📈"].map((icon, i) => (
                    <div
                      key={i}
                      className="grid h-12 place-items-center rounded-2xl bg-white/10 ring-1 ring-white/10"
                    >
                      {icon}
                    </div>
                  ))}
                </div>
              </div>
            </motion.div>

            {/* Right feature */}
            <motion.div whileHover={{ y: -4 }} className="rounded-[28px] bg-white/10 p-6 ring-1 ring-white/15">
              <div className="text-xl font-bold">Data Analytics For Finance</div>
              <p className="mt-2 text-sm text-white/70">
                Daily & monthly breakdowns, heatmaps, trends, insights, and overspend alerts.
              </p>

              <div className="mt-6 rounded-3xl bg-black/20 p-6 ring-1 ring-white/10">
                <div className="flex items-center justify-between">
                  <div className="text-sm font-semibold text-white/90">
                    Project Overview
                  </div>
                  <div className="rounded-xl bg-white/15 px-3 py-2 text-xs text-white/80 ring-1 ring-white/10">
                    This Year ▾
                  </div>
                </div>

                <div className="mt-6 grid grid-cols-12 items-end gap-2">
                  {Array.from({ length: 12 }).map((_, i) => {
                    const h = 18 + ((i * 17 + 10) % 90);
                    const isAccent = i === 6;
                    return (
                      <div
                        key={i}
                        className={[
                          "rounded-full ring-1",
                          isAccent
                            ? "bg-lime-300/85 ring-lime-200/40"
                            : "bg-white/12 ring-white/10",
                        ].join(" ")}
                        style={{ height: h }}
                      />
                    );
                  })}
                </div>

                <div className="mt-6 rounded-2xl bg-white/95 p-4 text-[#061a12] shadow-xl">
                  <div className="text-xs text-black/60">Main Balance</div>
                  <div className="mt-1 flex items-end justify-between">
                    <div className="text-lg font-extrabold">৳73,800</div>
                    <div className="text-xs text-black/50">90%</div>
                  </div>
                  <div className="mt-3 h-2 w-full rounded-full bg-black/10">
                    <div className="h-2 w-[90%] rounded-full bg-lime-400" />
                  </div>
                </div>
              </div>
            </motion.div>
          </motion.div>

          {/* Feature tiles */}
          <motion.div variants={fadeUp} className="mt-12 rounded-[32px] bg-white/10 p-8 ring-1 ring-white/15">
            <h3 className="text-center text-2xl font-extrabold tracking-tight md:text-3xl">
              You'll Find Everything You <span className="text-lime-200">Need</span> And More
            </h3>
            <p className="mx-auto mt-3 max-w-2xl text-center text-sm text-white/75">
              A full budgeting stack: categories, targets, goals, alerts, analytics, recurring bills, and family budgeting.
            </p>

            <div className="mt-8 grid gap-5 md:grid-cols-3">
              {featureTiles.map((f) => (
                <motion.div key={f.title} whileHover={{ y: -4 }} transition={{ type: "spring", stiffness: 260, damping: 18 }}>
                  <FeatureCard {...f} />
                </motion.div>
              ))}
            </div>
          </motion.div>
        </div>
      </motion.section>

      {/* PRICING */}
      <motion.section
        id="pricing"
        className="bg-[#072519]"
        initial="hidden"
        whileInView="visible"
        viewport={{ once: true, amount: 0.2 }}
        variants={stagger}
      >
        <div className="mx-auto max-w-6xl px-4 py-16">
          <motion.h2 variants={fadeUp} className="text-center text-3xl font-extrabold tracking-tight md:text-4xl">
            Prices That Are Simple <span className="text-lime-200">And Transparent</span>
          </motion.h2>

          <motion.p variants={fadeUp} className="mx-auto mt-4 max-w-2xl text-center text-sm text-white/75 md:text-base">
            No hidden fees—choose a plan and start budgeting smarter.
          </motion.p>

          <motion.div variants={fadeUp} className="mt-8 flex items-center justify-center">
            <div className="inline-flex items-center gap-2 rounded-full bg-white/12 p-1 ring-1 ring-white/15">
              <button
                onClick={() => setPricingMode("MONTHLY")}
                className={[
                  "rounded-full px-4 py-2 text-sm font-semibold transition",
                  pricingMode === "MONTHLY"
                    ? "bg-white text-[#061a12]"
                    : "text-white/75 hover:text-white",
                ].join(" ")}
              >
                Monthly
              </button>
              <button
                onClick={() => setPricingMode("ANNUAL")}
                className={[
                  "rounded-full px-4 py-2 text-sm font-semibold transition flex items-center gap-2",
                  pricingMode === "ANNUAL"
                    ? "bg-lime-300 text-[#061a12]"
                    : "text-white/75 hover:text-white",
                ].join(" ")}
              >
                Annually
                {pricing.badge ? (
                  <span className="rounded-full bg-[#061a12] px-2 py-0.5 text-xs text-lime-200">
                    {pricing.badge}
                  </span>
                ) : null}
              </button>
            </div>
          </motion.div>

          <motion.div variants={fadeUp} className="mt-10 grid gap-6 md:grid-cols-3">
            <PriceCard
              title="Basic"
              price={pricing.basic.price}
              suffix={pricing.basic.suffix}
              highlight={false}
              items={[
                "Track expenses & categorize spending",
                "Set monthly budgets & savings goals",
                "Automated reports",
                "Secure JWT auth",
                "Basic insights",
              ]}
            />
            <PriceCard
              title="Business"
              price={pricing.business.price}
              suffix={pricing.business.suffix}
              highlight={true}
              items={[
                "Everything in Basic",
                "Daily & monthly analytics",
                "Overspend alerts & insights",
                "Recurring bills automation",
                "Goals + leftover allocation",
              ]}
            />
            <PriceCard
              title="Pro"
              price={pricing.pro.price}
              suffix={pricing.pro.suffix}
              highlight={false}
              items={[
                "Everything in Business",
                "Family budgeting & roles",
                "Invites + join/leave flows",
                "Shared family categories/budgets",
                "Priority support",
              ]}
            />
          </motion.div>
        </div>
      </motion.section>

      {/* TESTIMONIALS */}
      <motion.section
        className="bg-[#083023]"
        initial="hidden"
        whileInView="visible"
        viewport={{ once: true, amount: 0.18 }}
        variants={stagger}
      >
        <div className="mx-auto max-w-6xl px-4 py-16">
          <motion.h2 variants={fadeUp} className="text-center text-3xl font-extrabold tracking-tight md:text-4xl">
            Client Feedback That <span className="text-lime-200">Speaks Volume</span>
          </motion.h2>

          <motion.p variants={fadeUp} className="mx-auto mt-4 max-w-2xl text-center text-sm text-white/75 md:text-base">
            Real users love clarity, control, and insights that actually change habits.
          </motion.p>

          <motion.div variants={fadeUp} className="mt-10 grid gap-6 md:grid-cols-2">
            {testimonials.map((t) => (
              <motion.div key={t.name} whileHover={{ y: -4 }} transition={{ type: "spring", stiffness: 260, damping: 18 }}>
                <TestimonialCard {...t} />
              </motion.div>
            ))}
          </motion.div>
        </div>
      </motion.section>

      {/* FAQ */}
      <motion.section
        id="faq"
        className="bg-[#072519]"
        initial="hidden"
        whileInView="visible"
        viewport={{ once: true, amount: 0.2 }}
        variants={stagger}
      >
        <div className="mx-auto max-w-6xl px-4 py-16">
          <div className="grid gap-10 md:grid-cols-2 md:items-start">
            <motion.div variants={fadeUp}>
              <h2 className="text-3xl font-extrabold tracking-tight md:text-4xl">
                Frequently Asked <span className="text-lime-200">Questions</span>?
              </h2>
              <p className="mt-4 text-sm text-white/75 md:text-base">
                Quick answers about features, security, and how your app works.
              </p>

              <div className="mt-7">
                <GlowButton variant="primary" onClick={() => navigate("/signup")}>
                  Start Free
                </GlowButton>
              </div>
            </motion.div>

            <motion.div variants={fadeUp} className="rounded-[28px] bg-white/10 p-4 ring-1 ring-white/15">
              {faq.map((item, idx) => {
                const open = openFaq === idx;
                return (
                  <div
                    key={item.q}
                    className={[
                      "rounded-2xl bg-black/20 ring-1 ring-white/10",
                      idx === 0 ? "" : "mt-3",
                    ].join(" ")}
                  >
                    <button
                      onClick={() => setOpenFaq(open ? -1 : idx)}
                      className="flex w-full items-center justify-between gap-4 px-5 py-4 text-left"
                    >
                      <span className="text-sm font-semibold text-white/90">
                        {item.q}
                      </span>
                      <span className="grid h-8 w-8 place-items-center rounded-full bg-white/12 ring-1 ring-white/10">
                        {open ? "—" : "+"}
                      </span>
                    </button>
                    {open ? (
                      <div className="px-5 pb-5 text-sm text-white/75">
                        {item.a}
                      </div>
                    ) : null}
                  </div>
                );
              })}
            </motion.div>
          </div>
        </div>
      </motion.section>

      {/* FOOTER */}
      <footer id="contact" className="border-t border-white/10 bg-[#083023]">
        <div className="mx-auto max-w-6xl px-4 py-14">
          <div className="grid gap-10 md:grid-cols-4">
            <div className="md:col-span-2">
              <div className="flex items-center gap-2 font-semibold">
                <span className="grid h-9 w-9 place-items-center rounded-xl bg-lime-300/20 ring-1 ring-lime-200/25">
                  <LogoMark />
                </span>
                <span className="text-lg">
                  Hishab <span className="text-lime-200">Nikash</span>
                </span>
              </div>
              <p className="mt-3 max-w-md text-sm text-white/75">
                A modern budgeting experience with analytics, goals, recurring bills,
                and family sharing—powered by your Spring Boot backend.
              </p>

              <div className="mt-5 flex flex-wrap gap-3">
                <Link
                  to="/login"
                  className="rounded-full bg-white/12 px-5 py-2 text-sm font-semibold text-white ring-1 ring-white/15 hover:bg-white/18 transition"
                >
                  Login
                </Link>
                <Link
                  to="/signup"
                  className="rounded-full bg-lime-300 px-5 py-2 text-sm font-semibold text-[#061a12] hover:bg-lime-200 transition"
                >
                  Sign Up
                </Link>
              </div>
            </div>

            <FooterCol
              title="Pages"
              links={[
                { label: "Home", onClick: () => scrollToId("home") },
                { label: "Features", onClick: () => scrollToId("features") },
                { label: "Pricing", onClick: () => scrollToId("pricing") },
                { label: "FAQ", onClick: () => scrollToId("faq") },
                { label: "Contact", onClick: () => scrollToId("contact") },
              ]}
            />
            <FooterCol
              title="Legal"
              links={[
                { label: "Terms of Use", onClick: noop },
                { label: "Privacy Policy", onClick: noop },
                { label: "Changelog", onClick: noop },
              ]}
            />
          </div>

          <div className="mt-10 flex flex-col gap-2 border-t border-white/10 pt-6 text-xs text-white/60 md:flex-row md:items-center md:justify-between">
            <div>© {new Date().getFullYear()} Hishab Nikash. All rights reserved.</div>
            <div className="flex gap-4">
              <button className="hover:text-white" onClick={noop}>
                Facebook
              </button>
              <button className="hover:text-white" onClick={noop}>
                Twitter
              </button>
              <button className="hover:text-white" onClick={noop}>
                Instagram
              </button>
              <button className="hover:text-white" onClick={noop}>
                LinkedIn
              </button>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}

/* ------------------------- motion variants ------------------------- */

const fadeUp = {
  hidden: { opacity: 0, y: 14 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.55, ease: "easeOut" } },
};

const stagger = {
  hidden: {},
  visible: { transition: { staggerChildren: 0.08 } },
};

/* ------------------------- helpers/components ------------------------- */

function scrollToId(id) {
  const el = document.getElementById(id);
  if (!el) return;
  el.scrollIntoView({ behavior: "smooth", block: "start" });
}

function noop() {}

function GlowButton({ children, onClick, variant = "primary" }) {
  const base =
    "relative inline-flex items-center justify-center gap-2 rounded-full px-6 py-3 text-sm font-semibold transition will-change-transform";

  const styles =
    variant === "primary"
      ? "bg-lime-300 text-[#061a12] hover:bg-lime-200 shadow-[0_10px_30px_rgba(190,242,100,0.28)]"
      : "bg-white/12 text-white ring-1 ring-white/15 hover:bg-white/18";

  return (
    <motion.button
      onClick={onClick}
      whileHover={{ scale: 1.07 }}
      whileTap={{ scale: 0.95 }}
      transition={{ type: "spring", stiffness: 420, damping: 18 }}
      className={`${base} ${styles} group overflow-hidden`}
    >
      {/* shimmer */}
      <span className="pointer-events-none absolute inset-0 opacity-0 group-hover:opacity-100 transition">
        <span className="absolute -left-1/2 top-0 h-full w-1/2 rotate-12 bg-white/25 blur-md animate-[shimmer_1.2s_linear_infinite]" />
      </span>

      {/* glow ring */}
      <span className="pointer-events-none absolute inset-0 rounded-full ring-1 ring-white/10" />

      {/* content */}
      <span className="relative z-10 flex items-center gap-2">
        {children}
        <motion.span
          initial={{ x: 0 }}
          whileHover={{ x: 4 }}
          transition={{ type: "spring", stiffness: 300 }}
        >
          <ArrowRight />
        </motion.span>
      </span>
    </motion.button>
  );
}

function StatCard({ label, value }) {
  return (
    <div className="rounded-2xl bg-white/10 p-4 ring-1 ring-white/15">
      <div className="text-xs text-white/70">{label}</div>
      <div className="mt-2 text-sm font-semibold text-white/95">{value}</div>
    </div>
  );
}

function Pill({ active, text }) {
  return (
    <div
      className={[
        "rounded-2xl px-3 py-2 text-center text-xs font-semibold",
        active ? "bg-lime-300 text-[#061a12]" : "text-white/75",
      ].join(" ")}
    >
      {text}
    </div>
  );
}

function FeatureCard({ title, desc, accent }) {
  const accentStyles =
    accent === "lime"
      ? "bg-lime-300/20 ring-lime-200/25"
      : "bg-emerald-300/15 ring-emerald-200/15";

  return (
    <div className="rounded-[26px] bg-white/10 p-5 ring-1 ring-white/15 hover:bg-white/12 transition">
      <div className="flex items-start gap-3">
        <div className={`grid h-11 w-11 place-items-center rounded-2xl ring-1 ${accentStyles}`}>
          <span className="text-lg">✦</span>
        </div>
        <div>
          <div className="text-sm font-bold text-white/95">{title}</div>
          <div className="mt-1 text-sm text-white/70">{desc}</div>
        </div>
      </div>
    </div>
  );
}

function PriceCard({ title, price, suffix, highlight, items }) {
  return (
    <div
      className={[
        "rounded-[30px] p-7 ring-1 transition",
        highlight
          ? "bg-[#115a3a] ring-lime-200/25 shadow-[0_20px_60px_rgba(190,242,100,0.10)]"
          : "bg-white/10 ring-white/15",
      ].join(" ")}
    >
      <div className="text-sm font-semibold text-white/85">{title}</div>
      <div className="mt-3 flex items-end gap-2">
        <div
          className={[
            "text-4xl font-extrabold tracking-tight",
            highlight ? "text-lime-200" : "text-white",
          ].join(" ")}
        >
          {price}
        </div>
        <div className="pb-1 text-sm text-white/70">{suffix}</div>
      </div>

      <div className="mt-5">
        <Link
          to="/signup"
          className={[
            "inline-flex w-full items-center justify-center gap-2 rounded-full px-5 py-3 text-sm font-semibold transition",
            highlight
              ? "bg-lime-300 text-[#061a12] hover:bg-lime-200"
              : "bg-white/12 text-white ring-1 ring-white/15 hover:bg-white/18",
          ].join(" ")}
        >
          Get started now <ArrowRight />
        </Link>
      </div>

      <ul className="mt-6 space-y-3 text-sm text-white/75">
        {items.map((it) => (
          <li key={it} className="flex items-start gap-2">
            <span className="mt-1 text-lime-200">•</span>
            <span>{it}</span>
          </li>
        ))}
      </ul>
    </div>
  );
}

function TestimonialCard({ name, title, quote, stars }) {
  return (
    <div className="rounded-[28px] bg-white/10 p-7 ring-1 ring-white/15">
      <div className="flex gap-1 text-lime-200">
        {Array.from({ length: stars }).map((_, i) => (
          <span key={i}>★</span>
        ))}
      </div>

      <p className="mt-4 text-sm leading-6 text-white/80">“{quote}”</p>

      <div className="mt-6 flex items-center gap-3">
        <Avatar initials={initials(name)} />
        <div>
          <div className="text-sm font-semibold text-white/95">{name}</div>
          <div className="text-xs text-white/70">{title}</div>
        </div>
      </div>
    </div>
  );
}

function FooterCol({ title, links }) {
  return (
    <div>
      <div className="text-sm font-semibold text-white/85">{title}</div>
      <div className="mt-4 space-y-3 text-sm text-white/75">
        {links.map((l) => (
          <button
            key={l.label}
            onClick={l.onClick}
            className="block hover:text-white transition"
          >
            {l.label}
          </button>
        ))}
      </div>
    </div>
  );
}

function Avatar({ initials }) {
  return (
    <div className="grid h-11 w-11 place-items-center rounded-full bg-white/12 ring-1 ring-white/15 text-sm font-bold text-white">
      {initials}
    </div>
  );
}

function initials(name) {
  const parts = String(name).trim().split(/\s+/);
  return (parts[0]?.[0] || "H") + (parts[1]?.[0] || "N");
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

function Gauge() {
  return (
    <svg width="260" height="150" viewBox="0 0 260 150" fill="none" aria-hidden="true">
      <path
        d="M30 130a100 100 0 0 1 200 0"
        stroke="rgba(255,255,255,0.12)"
        strokeWidth="22"
        strokeLinecap="round"
      />
      <path
        d="M30 130a100 100 0 0 1 200 0"
        stroke="rgba(190,242,100,0.90)"
        strokeWidth="22"
        strokeLinecap="round"
        strokeDasharray="230 400"
      />
      <path
        d="M30 130a100 100 0 0 1 200 0"
        stroke="rgba(14,58,32,0.95)"
        strokeWidth="22"
        strokeLinecap="round"
        strokeDasharray="90 400"
      />
      <circle cx="130" cy="130" r="6" fill="rgba(190,242,100,0.95)" />
      <path
        d="M80 110c15-20 25 10 40-10 10-12 20-6 30 6"
        stroke="rgba(255,255,255,0.75)"
        strokeWidth="3"
        strokeLinecap="round"
      />
    </svg>
  );
}
