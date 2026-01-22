import { Link } from "react-router-dom";
import { motion } from "framer-motion";

export default function VerifyEmailInfo() {
  return (
    <div className="min-h-screen bg-gradient-to-b from-[#0f3b28] via-[#0b2f21] to-[#062016] text-white flex items-center justify-center px-4">
      <motion.div
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        className="max-w-md w-full rounded-3xl bg-white/10 backdrop-blur-xl ring-1 ring-white/15 p-8 text-center shadow-[0_30px_80px_rgba(0,0,0,0.35)]"
      >
        <h1 className="text-3xl font-extrabold">Verify your email 📧</h1>

        <p className="mt-4 text-sm text-white/75">
          We’ve sent a verification link to your email address.
          <br />
          Please click the link to activate your account.
        </p>

        <p className="mt-3 text-xs text-white/55">
          You won’t be able to log in until your email is verified.
        </p>

        <Link
          to="/login"
          className="mt-6 inline-block rounded-2xl bg-lime-300 px-6 py-3 text-sm font-semibold text-[#061a12] hover:bg-lime-200 transition"
        >
          Go to Login
        </Link>
      </motion.div>
    </div>
  );
}
