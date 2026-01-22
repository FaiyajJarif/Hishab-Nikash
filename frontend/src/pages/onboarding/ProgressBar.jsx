export default function ProgressBar({ current, total }) {
    const percent = Math.round(((current + 1) / total) * 100);
  
    return (
      <div className="h-2 w-full rounded-full bg-white/10 overflow-hidden">
        <div
          className="h-full bg-lime-300 transition-all duration-500"
          style={{ width: `${percent}%` }}
        />
      </div>
    );
  }
  