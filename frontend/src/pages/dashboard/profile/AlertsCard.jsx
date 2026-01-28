import { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";

export default function AlertsCard({ alerts = [] }) {
    const [open, setOpen] = useState(false);
    const visible = alerts.slice(0, 5);
    const hasMore = alerts.length > 5;
  
    return (
      <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15 space-y-4">
        <div className="flex items-center justify-between">
          <div className="text-sm font-semibold text-white/85">🚨 Alerts</div>
          <div className="text-xs text-white/55">Latest</div>
        </div>
  
        {!alerts.length ? (
          <div className="text-sm text-white/60 py-4 text-center">
            No alerts right now 🎉
          </div>
        ) : (
          <motion.div
            initial="hidden"
            animate="show"
            variants={{
              hidden: {},
              show: {
                transition: { staggerChildren: 0.06 }
              }
            }}
            className="space-y-2"
          >
            {visible.map((a) => (
              <motion.div
                key={a.id}
                variants={{
                  hidden: { opacity: 0, y: 8 },
                  show: { opacity: 1, y: 0 }
                }}
                className={`rounded-2xl px-4 py-3 ring-1 ${alertStyle(a)}`}
              >
                <div className="text-sm">{a.message}</div>
                <div className="mt-1 text-xs text-white/50">
                  {timeAgo(a.createdAt)}
                </div>
              </motion.div>
            ))}
          </motion.div>
        )}
  
        {hasMore && (
          <button
            onClick={() => setOpen(true)}
            className="w-full mt-2 rounded-xl bg-white/10 py-2 text-xs hover:bg-white/20"
          >
            View all alerts
          </button>
        )}
  
        {/* 🌑 MODAL */}
        <AnimatePresence>
          {open && (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              className="fixed inset-0 z-50 bg-black/70 flex items-center justify-center"
              onClick={() => setOpen(false)}
            >
              <motion.div
                initial={{ scale: 0.95, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                exit={{ scale: 0.95, opacity: 0 }}
                transition={{ type: "spring", stiffness: 260, damping: 20 }}
                onClick={(e) => e.stopPropagation()}
                className="w-full max-w-lg rounded-3xl bg-[#061a12] p-6 ring-1 ring-white/15"
              >
                <div className="text-lg font-bold mb-4">🚨 All Alerts</div>
  
                <div className="space-y-2 max-h-[60vh] overflow-y-auto">
                  {alerts.map((a) => (
                    <div
                      key={a.id}
                      className={`rounded-xl px-4 py-3 ring-1 ${alertStyle(a)}`}
                    >
                      <div className="text-sm">{a.message}</div>
                      <div className="text-xs text-white/50 mt-1">
                        {timeAgo(a.createdAt)}
                      </div>
                    </div>
                  ))}
                </div>
  
                <button
                  onClick={() => setOpen(false)}
                  className="mt-4 w-full rounded-xl bg-white/10 py-2"
                >
                  Close
                </button>
              </motion.div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    );
  }
  
  
  function timeAgo(iso) {
    if (!iso) return "";
    const t = new Date(iso).getTime();
    const diff = Math.floor((Date.now() - t) / 1000);
  
    if (diff < 60) return "just now";
    if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;
    return `${Math.floor(diff / 86400)}d ago`;
  }
  
  function alertStyle(alert) {
    switch (alert.severity) {
      case "CRITICAL":
        return "bg-red-500/10 ring-red-400/30 text-red-300";
      case "WARNING":
        return "bg-yellow-500/10 ring-yellow-400/30 text-yellow-300";
      case "INFO":
      default:
        return "bg-blue-500/10 ring-blue-400/30 text-blue-300";
    }
  }
  
  