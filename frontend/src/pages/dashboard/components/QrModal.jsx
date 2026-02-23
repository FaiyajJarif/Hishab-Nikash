import { useState } from "react";
import { walletApi } from "../../../api/walletApi";

export default function QrModal({ open, onClose }) {
  const [amount, setAmount] = useState("");
  const [qr, setQr] = useState(null);

  if (!open) return null;

  async function generate() {
    const res = await walletApi.generateQr({
      amount: amount,
    });

    setQr(res.data);
  }

  return (
    <div className="modal">
      <h3>Generate QR</h3>

      <input
        type="number"
        value={amount}
        onChange={(e) => setAmount(e.target.value)}
      />

      <button onClick={generate}>
        Generate
      </button>

      {qr && (
        <img
          src={`data:image/png;base64,${qr}`}
          alt="QR"
        />
      )}
    </div>
  );
}
