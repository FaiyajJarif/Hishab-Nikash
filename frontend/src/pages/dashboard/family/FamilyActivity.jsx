import { motion, AnimatePresence } from "framer-motion";

export default function FamilyActivity({ activity = [] }) {
  return (
    <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15">
      <div className="mb-4 flex items-center justify-between">
        <div className="text-sm font-semibold text-white/85">
          🕒 Family Activity
        </div>
        <div className="text-xs text-white/60">
          Live updates
        </div>
      </div>

      <div className="space-y-2">
        <AnimatePresence initial={false}>
          {activity.map((e, i) => (
            <motion.div
              key={`${e.type}-${e.at}-${i}`}
              initial={{ opacity: 0, y: -6 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0 }}
              transition={{ duration: 0.25 }}
              className="flex items-start gap-3 rounded-2xl bg-white/5 px-4 py-3 ring-1 ring-white/10"
            >
              <div className="mt-0.5 text-lg">
                {iconFor(e.type)}
              </div>

              <div className="flex-1">
                <div className="text-sm text-white/85">
                  {e.message}
                </div>
                <div className="mt-0.5 text-xs text-white/50">
                  {e.actorName ? `by ${e.actorName} · ` : ""}
                  {timeAgo(e.at)}
                </div>
              </div>
            </motion.div>
          ))}
        </AnimatePresence>

        {!activity.length && (
          <div className="text-sm text-white/60 py-4 text-center">
            No activity yet — changes will appear here in real time.
          </div>
        )}
      </div>
    </div>
  );
}

/* ---------------- helpers ---------------- */

function iconFor(type = "") {
  if (type.includes("EXPENSE")) return "💸";
  if (type.includes("INCOME")) return "💰";
  if (type.includes("BUDGET")) return "📊";
  if (type.includes("INVITE")) return "📩";
  if (type.includes("ACCEPT")) return "✅";
  if (type.includes("REJECT")) return "❌";
  if (type.includes("MEMBER")) return "👤";
  return "🔔";
}


function timeAgo(iso) {
  if (!iso) return "";
  const diff = Math.floor((Date.now() - new Date(iso)) / 1000);

  if (diff < 60) return "just now";
  if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
  if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;
  return `${Math.floor(diff / 86400)}d ago`;
}
