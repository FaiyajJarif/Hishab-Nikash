import { useSearchParams, Link } from "react-router-dom";
import { motion } from "framer-motion";

export default function VerifyEmailPage() {
  const [params] = useSearchParams();
  const status = params.get("status");

  const config = {
    success: {
      title: "Email verified 🎉",
      message: "Your account is now active. You can log in.",
      color: "lime",
    },
    expired: {
      title: "Link expired ⏰",
      message: "This verification link has expired.",
      color: "yellow",
    },
    invalid: {
      title: "Invalid link ❌",
      message: "This verification link is not valid.",
      color: "red",
    },
  };

  const data = config[status] || config.invalid;

  return (
    <div className="min-h-screen bg-gradient-to-b from-[#0f3b28] via-[#0b2f21] to-[#062016] text-white flex items-center justify-center px-4">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="max-w-md w-full rounded-3xl bg-white/10 backdrop-blur-xl ring-1 ring-white/15 shadow-[0_30px_80px_rgba(0,0,0,0.35)] p-8 text-center"
      >
        <h1 className={`text-3xl font-extrabold text-${data.color}-200`}>
          {data.title}
        </h1>

        <p className="mt-4 text-white/75">{data.message}</p>

        <Link
          to="/login"
          className="mt-8 inline-block rounded-2xl bg-lime-300 px-6 py-3 text-sm font-semibold text-[#061a12] hover:bg-lime-200 transition"
        >
          Go to Login →
        </Link>
      </motion.div>
    </div>
  );
}
