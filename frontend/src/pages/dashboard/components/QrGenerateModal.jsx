import { useState } from "react";
import toast from "react-hot-toast";
import { walletApi } from "../../../api/walletApi";

export default function QrGenerateModal({ open, onClose }) {
  const [amount, setAmount] = useState("");
  const [qrImage, setQrImage] = useState(null);
  const [loading, setLoading] = useState(false);

  if (!open) return null;

  async function handleGenerate() {
    if (!amount || Number(amount) <= 0) {
      toast.error("Enter valid amount");
      return;
    }

    try {
      setLoading(true);

      const res = await walletApi.generateQr({
        amount: amount,
      });

      setQrImage(res.data);
    } catch (err) {
      toast.error("QR generation failed");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-50">
      <div className="bg-[#061a12] rounded-3xl p-6 w-[420px] ring-1 ring-white/15 space-y-4">

        <div className="text-lg font-bold text-lime-200">
          Generate Payment QR
        </div>

        {!qrImage ? (
          <>
            <input
              type="number"
              placeholder="Enter amount"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              className="w-full px-4 py-2 rounded-xl bg-white/10 text-white"
            />

            <button
              onClick={handleGenerate}
              disabled={loading}
              className="w-full px-4 py-2 rounded-xl bg-lime-300 text-[#061a12] font-semibold"
            >
              {loading ? "Generating..." : "Generate QR"}
            </button>
          </>
        ) : (
          <div className="space-y-4 text-center">
            <img
              src={`data:image/png;base64,${qrImage}`}
              alt="QR"
              className="mx-auto"
            />

            <button
              onClick={() => {
                setQrImage(null);
                setAmount("");
              }}
              className="text-sm text-white/60"
            >
              Generate another
            </button>
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
      </div>
    </div>
  );
}
