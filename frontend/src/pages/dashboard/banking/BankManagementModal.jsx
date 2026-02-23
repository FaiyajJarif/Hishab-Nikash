import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { bankingApi } from "../../../api/bankingApi";
import AddMockBankModal from "./AddMockBankModal";

export default function BankManagementModal({ open, onClose }) {
  const [banks, setBanks] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [activeTab, setActiveTab] = useState("banks");
  const [loading, setLoading] = useState(false);

  const [withdrawAmounts, setWithdrawAmounts] = useState({});
  const [topUpAmounts, setTopUpAmounts] = useState({});

  const [transferForm, setTransferForm] = useState({
    fromBankId: "",
    toBankId: "",
    amount: ""
  });
  const [createOpen, setCreateOpen] = useState(false);

  /* ------------------ LOAD DATA ------------------ */

  async function loadData() {
    try {
      setLoading(true);
      const list = await bankingApi.getBanks();
      const txns = await bankingApi.getBankTransactions();
      setBanks(list);
      setTransactions(txns);
    } catch {
      toast.error("Failed to load banks");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (open) loadData();
  }, [open]);

  if (!open) return null;

  /* ------------------ ACTIONS ------------------ */

  async function handleWithdraw(bank) {
    const amount = withdrawAmounts[bank.id];
    if (!amount) return toast.error("Enter withdraw amount");

    try {
      await bankingApi.withdraw({
        provider: bank.provider,
        accountNumber: bank.accountNumber,
        amount
      });

      toast.success("Withdrawal successful");

      setWithdrawAmounts(prev => ({
        ...prev,
        [bank.id]: ""
      }));

      loadData();
    } catch {
      toast.error("Withdrawal failed");
    }
  }

  async function handleTopUp(bank) {
    const amount = topUpAmounts[bank.id];
    if (!amount) return toast.error("Enter top up amount");

    try {
      await bankingApi.topUpFromBank({
        bankAccountId: bank.id, // ✅ FIXED
        amount
      });

      toast.success("Top-up successful");

      setTopUpAmounts(prev => ({
        ...prev,
        [bank.id]: ""
      }));

      loadData();
    } catch {
      toast.error("Top-up failed");
    }
  }

  async function handleTransferBetweenBanks() {
    const { fromBankId, toBankId, amount } = transferForm;

    if (!fromBankId || !toBankId || !amount) {
      return toast.error("Fill all transfer fields");
    }

    if (fromBankId === toBankId) {
      return toast.error("Cannot transfer to same bank");
    }

    const fromBank = banks.find(b => b.id === Number(fromBankId));
    const toBank = banks.find(b => b.id === Number(toBankId));

    if (!fromBank || !toBank) {
      return toast.error("Invalid bank selection");
    }

    try {
      await bankingApi.transferBetweenBanks({
        fromProvider: fromBank.provider,
        fromAccountNumber: fromBank.accountNumber,
        toProvider: toBank.provider,
        toAccountNumber: toBank.accountNumber,
        amount
      });

      toast.success("Bank transfer successful");

      setTransferForm({
        fromBankId: "",
        toBankId: "",
        amount: ""
      });

      loadData();
    } catch {
      toast.error("Transfer failed");
    }
  }

  async function handleSync() {
    try {
      await bankingApi.sync();
      toast.success("Banks synced");
      loadData();
    } catch {
      toast.error("Sync failed");
    }
  }

  /* ------------------ UI ------------------ */

  return (
    <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-50">
        <div className="relative bg-[#061a12] rounded-3xl w-[900px] max-h-[90vh] overflow-hidden ring-1 ring-white/15">

        {/* Header */}
        <div className="flex justify-between items-center p-6 border-b border-white/10">
          <div className="text-xl font-bold text-lime-200">
            Bank Management
          </div>

          <div className="flex gap-3">
            <button
              onClick={handleSync}
              className="bg-lime-300 text-[#061a12] px-4 py-1 rounded-xl text-sm font-semibold"
            >
              Sync Banks
            </button>

            <button onClick={onClose} className="text-white/60">
              Close
            </button>
          </div>
        </div>

        {/* Tabs */}
        <div className="flex gap-4 px-6 pt-4">
          <button
            onClick={() => setActiveTab("banks")}
            className={tabStyle(activeTab === "banks")}
          >
            Linked Banks
          </button>

          <button
            onClick={() => setActiveTab("transactions")}
            className={tabStyle(activeTab === "transactions")}
          >
            Bank Transactions
          </button>
        </div>

        {/* Content */}
        <div className="p-6 overflow-y-auto max-h-[70vh]">

          {loading && <div className="text-white/60">Loading...</div>}

          {/* BANKS TAB */}
          {!loading && activeTab === "banks" && (
            <div className="space-y-6">

              {banks.map((b) => (
                <div
                  key={b.id}
                  className="bg-white/5 p-5 rounded-2xl space-y-4"
                >
                  <div className="flex justify-between items-center">
                    <div>
                      <div className="font-semibold text-white">
                        {b.provider}
                      </div>
                      <div className="text-xs text-white/50">
                        {b.accountNumber}
                      </div>
                    </div>

                    <div className="text-right">
                      <div className="text-lime-200 font-bold text-lg">
                        ৳{Number(b.balance).toLocaleString()}
                      </div>

                      <StatusBadge status="CONNECTED" />
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-3">
                    {/* Withdraw */}
                    <div className="flex gap-2">
                      <input
                        type="number"
                        placeholder="Withdraw"
                        value={withdrawAmounts[b.id] || ""}
                        onChange={(e) =>
                          setWithdrawAmounts(prev => ({
                            ...prev,
                            [b.id]: e.target.value
                          }))
                        }
                        className="flex-1 bg-white/10 rounded-xl px-3 py-2 text-sm"
                      />
                      <button
                        onClick={() => handleWithdraw(b)}
                        className="bg-red-400 text-black px-4 rounded-xl text-sm"
                      >
                        Withdraw
                      </button>
                    </div>

                    {/* Top Up */}
                    <div className="flex gap-2">
                      <input
                        type="number"
                        placeholder="Top Up"
                        value={topUpAmounts[b.id] || ""}
                        onChange={(e) =>
                          setTopUpAmounts(prev => ({
                            ...prev,
                            [b.id]: e.target.value
                          }))
                        }
                        className="flex-1 bg-white/10 rounded-xl px-3 py-2 text-sm"
                      />
                      <button
                        onClick={() => handleTopUp(b)}
                        className="bg-lime-300 text-black px-4 rounded-xl text-sm"
                      >
                        Top Up
                      </button>
                    </div>
                  </div>
                </div>
              ))}

              {/* TRANSFER BETWEEN BANKS */}
              <div className="bg-white/5 p-6 rounded-2xl space-y-4">
                <div className="font-semibold text-white">
                  Transfer Between Banks
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <select
                    value={transferForm.fromBankId}
                    onChange={(e) =>
                      setTransferForm(prev => ({
                        ...prev,
                        fromBankId: e.target.value
                      }))
                    }
                    className="bg-white/10 px-3 py-2 rounded-xl"
                  >
                    <option value="">Select From Bank</option>
                    {banks.map(b => (
                      <option key={b.id} value={b.id}>
                        {b.provider} - {b.accountNumber}
                      </option>
                    ))}
                  </select>

                  <select
                    value={transferForm.toBankId}
                    onChange={(e) =>
                      setTransferForm(prev => ({
                        ...prev,
                        toBankId: e.target.value
                      }))
                    }
                    className="bg-white/10 px-3 py-2 rounded-xl"
                  >
                    <option value="">Select To Bank</option>
                    {banks.map(b => (
                      <option key={b.id} value={b.id}>
                        {b.provider} - {b.accountNumber}
                      </option>
                    ))}
                  </select>

                  <input
                    type="number"
                    placeholder="Amount"
                    value={transferForm.amount}
                    onChange={(e) =>
                      setTransferForm(prev => ({
                        ...prev,
                        amount: e.target.value
                      }))
                    }
                    className="bg-white/10 px-3 py-2 rounded-xl col-span-2"
                  />
                </div>

                <button
                  onClick={handleTransferBetweenBanks}
                  className="bg-lime-300 text-black px-6 py-2 rounded-xl font-semibold"
                >
                  Transfer
                </button>
              </div>
            </div>
          )}

          {/* TRANSACTIONS TAB */}
          {!loading && activeTab === "transactions" && (
            <div className="space-y-3">
              {transactions.map((t) => (
                <div
                  key={t.transactionId}
                  className="bg-white/5 p-4 rounded-2xl flex justify-between"
                >
                  <div>
                    <div className="text-sm font-medium text-white">
                      {t.type}
                    </div>
                    <div className="text-xs text-white/50">
                      {t.description}
                    </div>
                  </div>

                  <div className="text-right">
                    <div className={Number(t.amount) > 0 ? "text-lime-300" : "text-red-300"}>
                      ৳{Number(t.amount).toLocaleString()}
                    </div>
                    <div className="text-xs text-white/40">
                      {t.status}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}

        </div>
        {/* Floating Create Button */}
        {activeTab === "banks" && (
        <button
            onClick={() => setCreateOpen(true)}
            className="absolute bottom-6 right-6 w-14 h-14 rounded-full bg-lime-300 text-black text-2xl shadow-lg hover:scale-105 transition flex items-center justify-center"
        >
            +
        </button>
        )}

        <AddMockBankModal
        open={createOpen}
        onClose={() => setCreateOpen(false)}
        onSuccess={loadData}
        />
      </div>
    </div>
  );
}

/* ------------------ HELPERS ------------------ */

function tabStyle(active) {
  return `px-4 py-2 rounded-xl text-sm ${
    active
      ? "bg-lime-300 text-[#061a12] font-semibold"
      : "bg-white/10 text-white/60"
  }`;
}

function StatusBadge({ status }) {
  return (
    <span className="px-3 py-1 text-xs rounded-full bg-lime-400/20 text-lime-300 animate-pulse">
      {status}
    </span>
  );
}
