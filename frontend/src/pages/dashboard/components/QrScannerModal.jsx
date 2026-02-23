import { useState, useEffect } from "react";
import { Scanner } from "@yudiel/react-qr-scanner";
import toast from "react-hot-toast";
import { walletApi } from "../../../api/walletApi";

export default function QrScannerModal({ open, onClose, onSuccess }) {

  // ✅ ALL HOOKS FIRST (no early return above this)
  const [qrValue, setQrValue] = useState(null);
  const [parsed, setParsed] = useState(null);
  const [timeLeft, setTimeLeft] = useState(0);
  const [processing, setProcessing] = useState(false);

  // 🔥 Parse QR safely
  useEffect(() => {
    if (!qrValue) return;

    try {
      const cleaned = qrValue.trim().replace(/\s+/g, "");

      const parts = cleaned.split("::");
      if (parts.length !== 2) {
        throw new Error("Invalid QR structure");
      }

      const payload = parts[0];
      const signature = parts[1];

      const fields = payload.split("|");
      if (fields.length < 3) {
        throw new Error("Invalid payload format");
      }

      const email = fields[0];
      const amount = fields[1];
      const exp = fields[2];

      const expiry = Number(exp);
      if (isNaN(expiry)) {
        throw new Error("Invalid expiry");
      }

      setParsed({
        email,
        amount,
        expiry,
        signature,
      });

      setTimeLeft(expiry - Date.now());

    } catch (err) {
      console.error(err);
      toast.error("Invalid QR format");
      setQrValue(null);
    }
  }, [qrValue]);

  // 🔥 Countdown
  useEffect(() => {
    if (!parsed) return;

    const interval = setInterval(() => {
      const remaining = parsed.expiry - Date.now();
      setTimeLeft(remaining);
    }, 1000);

    return () => clearInterval(interval);
  }, [parsed]);

  async function handlePayment() {
    if (!parsed || timeLeft <= 0 || processing) return;

    try {
      setProcessing(true);

      await walletApi.payQr({
        qr: qrValue,
      });

      toast.success("Payment successful ✅");
      onSuccess?.();
      onClose();

    } catch (err) {
      toast.error(
        err?.response?.data?.error?.message || "Payment failed"
      );
    } finally {
      setProcessing(false);
    }
  }

  const expired = timeLeft <= 0;

  function formatTime(ms) {
    const totalSec = Math.max(0, Math.floor(ms / 1000));
    const min = Math.floor(totalSec / 60);
    const sec = totalSec % 60;
    return `${min}:${sec.toString().padStart(2, "0")}`;
  }

  // ✅ SAFE conditional render AFTER hooks
  if (!open) return null;

  return (
    <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-50">
      <div className="bg-[#061a12] rounded-3xl p-6 w-[420px] ring-1 ring-white/15 space-y-4">

        <div className="text-lg font-bold text-lime-200">
          Scan QR to Pay
        </div>

        {!parsed ? (
          <div className="rounded-2xl overflow-hidden border border-white/10">
            <Scanner
              onScan={(result) => {
                if (!result) return;

                const value = Array.isArray(result)
                  ? result[0]?.rawValue
                  : result.rawValue;

                if (value) {
                  setQrValue(value);
                }
              }}
              constraints={{ facingMode: "environment" }}
              styles={{ container: { width: "100%" } }}
            />
          </div>
        ) : (
          <div className="space-y-4">

            <div className="bg-white/10 p-4 rounded-2xl space-y-2">
              <div className="text-sm text-white/60">
                Receiver
              </div>
              <div className="font-semibold text-white">
                {parsed.email}
              </div>

              <div className="text-sm text-white/60 mt-3">
                Amount
              </div>
              <div className="text-2xl font-bold text-lime-200">
                ৳{Number(parsed.amount).toLocaleString()}
              </div>

              <div className="text-sm mt-3">
                {expired ? (
                  <span className="text-red-400">QR Expired</span>
                ) : (
                  <span className="text-yellow-300">
                    Expires in {formatTime(timeLeft)}
                  </span>
                )}
              </div>
            </div>

            <div className="flex justify-end gap-3">
              <button
                onClick={() => {
                  setParsed(null);
                  setQrValue(null);
                }}
                className="px-4 py-2 rounded-xl bg-white/10"
              >
                Rescan
              </button>

              <button
                disabled={expired || processing}
                onClick={handlePayment}
                className={`px-4 py-2 rounded-xl font-semibold ${
                  expired
                    ? "bg-gray-500 text-white/60"
                    : "bg-lime-300 text-[#061a12]"
                }`}
              >
                {processing ? "Processing..." : "Confirm Pay"}
              </button>
            </div>
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
