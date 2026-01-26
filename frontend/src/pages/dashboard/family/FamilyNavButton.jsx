import { useNavigate } from "react-router-dom";

export default function FamilyNavButton({ familyId }) {
  const navigate = useNavigate();

  return (
    <button
      onClick={() => navigate(`/dashboard/family/${familyId}`)}
      className="w-full text-left px-3 py-2 rounded-lg
                 hover:bg-white/10 transition"
    >
      👨‍👩‍👧 Family
    </button>
  );
}
