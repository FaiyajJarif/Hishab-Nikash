import { useState, useEffect } from "react";
import { bankingApi } from "../../../api/bankingApi";
import toast from "react-hot-toast";

export default function WithdrawModal({ open, onClose, banks, onSuccess }) {
  const [amount, setAmount] = useState("");
  const [bank, setBank] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (banks.length > 0) {
      setBank(banks[0]);
    }
  }, [banks]);

  if (!open) return null;

  async function handleWithdraw() {
    try {
      setLoading(true);

      await bankingApi.withdraw({
        provider: bank.provider,
        accountNumber: bank.accountNumber,
        amount: amount,
      });

      toast.success("Withdrawal successful");
      onSuccess();
      onClose();
    } catch (e) {
      toast.error("Withdrawal failed");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="modal">
      <h3>Withdraw to Bank</h3>

      <select
        onChange={(e) =>
          setBank(
            banks.find(
              (b) =>
                b.accountNumber === e.target.value
            )
          )
        }
      >
        {banks.map((b) => (
          <option
            key={b.bankAccountId}
            value={b.accountNumber}
          >
            {b.provider}
          </option>
        ))}
      </select>

      <input
        type="number"
        placeholder="Amount"
        value={amount}
        onChange={(e) => setAmount(e.target.value)}
      />

      <button onClick={handleWithdraw}>
        Confirm
      </button>
    </div>
  );
}
