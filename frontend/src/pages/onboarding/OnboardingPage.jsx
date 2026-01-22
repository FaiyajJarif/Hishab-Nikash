import { useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { apiRequest } from "../../lib/api";
import { steps } from "./steps";
import ProgressBar from "./ProgressBar";

export default function OnboardingPage() {
  const navigate = useNavigate();

  const [current, setCurrent] = useState(0);
  const [inputValue, setInputValue] = useState("");
  const [selected, setSelected] = useState([]);
  const step = steps[current];

  /* ---------- Resume onboarding ---------- */
  useEffect(() => {
    apiRequest("/api/onboarding/status").then((res) => {
      if (res.completed) {
        navigate("/dashboard");
      } else if (res.lastStep) {
        const idx = steps.findIndex(s => s.key === res.lastStep);
        if (idx >= 0) setCurrent(idx);
      }
    });
  }, []);

  /* ---------- Selection logic ---------- */
  function toggleOption(option) {
    setSelected((prev) =>
      prev.includes(option)
        ? prev.filter(o => o !== option)
        : [...prev, option]
    );
  }

  /* ---------- Save step ---------- */
  async function next() {
    let selections = [];

    if (step.type === "input") {
      if (!inputValue.trim()) return;
      selections = [{
        category: step.category,
        value: inputValue,
        frequency: null,
      }];
    } else {
      if (!selected.length) return;
      selections = selected.map(v => ({
        category: step.category,
        value: v,
        frequency: null,
      }));
    }

    await apiRequest("/api/onboarding/step", {
      method: "POST",
      body: {
        step: step.key,
        selections,
      },
    });

    setSelected([]);
    setInputValue("");

    if (current === steps.length - 1) {
      navigate("/dashboard");
    } else {
      setCurrent(c => c + 1);
    }
  }

  /* ---------- UI ---------- */
  return (
    <div className="min-h-screen bg-gradient-to-b from-[#0f3b28] to-[#062016] text-white flex items-center justify-center px-4">
      <motion.div
        initial={{ opacity: 0, scale: 0.96 }}
        animate={{ opacity: 1, scale: 1 }}
        className="w-full max-w-xl rounded-3xl bg-white/10 backdrop-blur-xl p-10 shadow-2xl ring-1 ring-white/15"
      >
        <ProgressBar current={current} total={steps.length} />

        <AnimatePresence mode="wait">
          <motion.div
            key={step.key}
            initial={{ opacity: 0, x: 30 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -30 }}
            transition={{ duration: 0.4 }}
          >
            <h1 className="text-3xl font-extrabold mt-6">
              {step.title}
            </h1>
            <p className="text-white/70 mt-2">
              {step.subtitle}
            </p>

            {/* INPUT */}
            {step.type === "input" && (
              <input
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                placeholder={step.placeholder}
                className="mt-8 w-full rounded-2xl bg-white/10 px-5 py-4 text-white ring-1 ring-white/15 focus:ring-lime-300 outline-none"
              />
            )}

            {/* OPTIONS */}
            {step.type === "multi" && (
              <div className="mt-8 grid grid-cols-2 gap-4">
                {step.options.map(opt => (
                  <motion.button
                    key={opt}
                    whileHover={{ scale: 1.05 }}
                    whileTap={{ scale: 0.95 }}
                    onClick={() => toggleOption(opt)}
                    className={`rounded-2xl px-4 py-4 text-sm ring-1 transition
                      ${
                        selected.includes(opt)
                          ? "bg-lime-300 text-[#062016] ring-lime-200"
                          : "bg-white/10 ring-white/15 hover:bg-white/15"
                      }`}
                  >
                    {opt}
                  </motion.button>
                ))}
              </div>
            )}
          </motion.div>
        </AnimatePresence>

        <motion.button
          whileHover={{ scale: 1.04 }}
          whileTap={{ scale: 0.96 }}
          onClick={next}
          className="mt-10 w-full rounded-2xl bg-lime-300 py-4 font-semibold text-[#062016]"
        >
          {current === steps.length - 1 ? "Finish 🎉" : "Continue →"}
        </motion.button>
      </motion.div>
    </div>
  );
}
