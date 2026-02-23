import { motion } from "framer-motion";

const BANK_LOGOS = {
BKASH: "https://cdn.brandfetch.io/id0tYkNh7H/w/400/h/400/theme/dark/logo.png",
  NAGAD: "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f7/Nagad_Logo.svg/512px-Nagad_Logo.svg.png",
  DBBL: "https://seeklogo.com/images/D/dutch-bangla-bank-logo-6C4B1A2D4F-seeklogo.com.png",
  NEXUS: "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/DBBL_Nexus_logo.svg/512px-DBBL_Nexus_logo.svg.png"
};

export default function ConnectedBanksModal({ open, onClose, banks }) {

  if (!open) return null;

  return (
    <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-50">
      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        className="bg-[#061a12] rounded-3xl p-6 w-[500px] ring-1 ring-white/15 space-y-6"
      >
        <div className="text-xl font-bold text-lime-200">
          Connected Banks
        </div>

        {banks.length === 0 ? (
          <div className="text-white/50 text-sm">
            No banks connected.
          </div>
        ) : (
          <div className="space-y-4">
            {banks.map((bank) => (
              <div
                key={bank.bankAccountId}
                className="flex items-center justify-between bg-white/5 p-4 rounded-2xl"
              >
                <div className="flex items-center gap-4">
                  
                  {/* Logo */}
                  <img
                    src={BANK_LOGOS[bank.provider] || ""}
                    alt={bank.provider}
                    className="w-10 h-10 object-contain bg-white rounded-lg p-1"
                  />

                  <div>
                    <div className="text-white font-semibold">
                      {bank.provider}
                    </div>
                    <div className="text-xs text-white/50">
                      {bank.accountNumber}
                    </div>
                  </div>
                </div>

                <div className="text-lime-200 font-bold">
                  ৳{Number(bank.balance).toLocaleString()}
                </div>
              </div>
            ))}
          </div>
        )}

        <div className="text-right">
          <button
            onClick={onClose}
            className="text-white/50 text-sm"
          >
            Close
          </button>
        </div>
      </motion.div>
    </div>
  );
}
