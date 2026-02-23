import { useState } from "react";
import { bankingApi } from "../../../api/bankingApi";
import toast from "react-hot-toast";

export default function AddMockBankModal({ open, onClose, onSuccess }) {
  const [form, setForm] = useState({
    provider: "",
    accountNumber: "",
    ownerName: "",
    balance: ""
  });

  if (!open) return null;

  async function handleCreate() {
    try {
      await bankingApi.createMockBank(form);
      toast.success("Mock bank created");
      onSuccess();
      onClose();
    } catch {
      toast.error("Creation failed");
    }
  }

  return (
    <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-50">
      <div className="bg-[#061a12] p-6 rounded-3xl w-[400px] space-y-4">

        <div className="text-lg font-semibold text-lime-200">
          Create Mock Bank
        </div>

        <select
          onChange={(e) =>
            setForm({ ...form, provider: e.target.value })
          }
          className="w-full bg-white/10 px-3 py-2 rounded-xl"
        >
          <option value="">Select Provider</option>
          <option value="BKASH">BKASH</option>
          <option value="NAGAD">NAGAD</option>
          <option value="DBBL">DBBL</option>
        </select>

        <input
          placeholder="Account Number"
          onChange={(e) =>
            setForm({ ...form, accountNumber: e.target.value })
          }
          className="w-full bg-white/10 px-3 py-2 rounded-xl"
        />

        <input
          placeholder="Owner Name"
          onChange={(e) =>
            setForm({ ...form, ownerName: e.target.value })
          }
          className="w-full bg-white/10 px-3 py-2 rounded-xl"
        />

        <input
          type="number"
          placeholder="Initial Balance"
          onChange={(e) =>
            setForm({ ...form, balance: e.target.value })
          }
          className="w-full bg-white/10 px-3 py-2 rounded-xl"
        />

        <div className="flex justify-between">
          <button
            onClick={onClose}
            className="text-white/60"
          >
            Cancel
          </button>

          <button
            onClick={handleCreate}
            className="bg-lime-300 text-black px-4 py-2 rounded-xl"
          >
            Create
          </button>
        </div>
      </div>
    </div>
  );
}
