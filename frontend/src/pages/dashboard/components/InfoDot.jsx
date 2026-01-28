export default function InfoDot({ text }) {
    return (
      <div className="group relative">
        <div className="h-5 w-5 rounded-full bg-lime-300 text-[#061a12] text-xs grid place-items-center cursor-pointer">
          i
        </div>
  
        <div className="absolute left-1/2 -translate-x-1/2 mt-2 w-56
                        rounded-xl bg-black/90 p-3 text-xs text-white
                        opacity-0 group-hover:opacity-100 transition pointer-events-none z-50">
          {text}
        </div>
      </div>
    );
  }
  