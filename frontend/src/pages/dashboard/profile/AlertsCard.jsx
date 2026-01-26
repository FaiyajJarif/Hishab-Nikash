export default function AlertsCard({ alerts = [] }) {
    return (
      <div className="rounded-3xl bg-white/10 p-6 ring-1 ring-white/15 space-y-4">
        <div className="flex items-center justify-between">
          <div className="text-sm font-semibold text-white/85">🚨 Alerts</div>
          <div className="text-xs text-white/55">Latest 10</div>
        </div>
  
        {!alerts.length ? (
          <div className="text-sm text-white/60 py-4 text-center">
            No alerts right now 🎉
          </div>
        ) : (
          <div className="space-y-2">
            {alerts.map((a) => (
              <div
                key={a.id}
                className="rounded-2xl bg-white/5 px-4 py-3 ring-1 ring-white/10"
              >
                <div className="text-sm text-white/85">{a.message}</div>
                <div className="mt-1 text-xs text-white/50">
                  {timeAgo(a.createdAt)}
                </div>
              </div>
            ))}
          </div>
        )}
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
  