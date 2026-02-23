import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import toast from "react-hot-toast";

import { useAlertStomp } from "../../../hooks/useAlertStomp";
import { walletApi } from "../../../api/walletApi";

import TransferModal from "../components/TransferModal";
import WalletHistoryTable from "../components/WalletHistoryTable";
import TopUpModal from "../components/TopUpModal";

import { bankingApi } from "../../../api/bankingApi";
import ConnectBankModal from "../banking/components/ConnectBankModal";

import { connectUserSocket, disconnectUserSocket } 
  from "../../../ws/userSocket";
import QrScannerModal from "../components/QrScannerModal";
import QrGenerateModal from "../components/QrGenerateModal";
import BankManagementModal from "../banking/BankManagementModal";

export default function BankingDashboard() {
  const [wallet, setWallet] = useState(null);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [transferOpen, setTransferOpen] = useState(false);
  const [fraudWarning, setFraudWarning] = useState(false);
  const [topUpOpen, setTopUpOpen] = useState(false);
  const [connectOpen, setConnectOpen] = useState(false);
  const [banks, setBanks] = useState([]);
  const [scanOpen, setScanOpen] = useState(false);
  const [qrGenerateOpen, setQrGenerateOpen] = useState(false);
  const [banksModalOpen, setBanksModalOpen] = useState(false);

  async function loadWallet() {
    try {
      setLoading(true);
      const res = await walletApi.getWallet();
      setWallet(res.data);
      const hist = await walletApi.getHistory(25);
      setHistory(hist.data);
    } catch (e) {
      toast.error("Failed to load wallet");
    } finally {
      setLoading(false);
    }
  }
  async function loadBanks() {
    try {
      const list = await bankingApi.getBanks();
      setBanks(list);
    } catch (err) {
      console.error(err);
      setBanks([]);
    }
  }  

  useEffect(() => {
    loadWallet();
    loadBanks();

    const refresh = () => loadWallet();
    window.addEventListener("wallet-refresh", refresh);
    return () => window.removeEventListener("wallet-refresh", refresh);
  }, []);

  useEffect(() => {
    const openConnect = () => setConnectOpen(true);
    window.addEventListener("open-connect-bank", openConnect);
    return () => window.removeEventListener("open-connect-bank", openConnect);
  }, []);

  useEffect(() => {
    if (!wallet?.userId) return;
  
    connectUserSocket(
      wallet.userId,
      (event) => {
        if (!event?.type) return;
  
        if (event.type === "WALLET_UPDATED") {
          loadWallet();
        }
  
        if (event.type === "FRAUD_ALERT") {
          setFraudWarning(true);
          toast.error("🚨 Suspicious transfer detected!");
          setTimeout(() => setFraudWarning(false), 8000);
        }
  
        if (event.type === "BANK_UPDATE") {
          toast.success(event.message);
          loadWallet();
          loadBanks();
        }
      },
      (status) => {
        console.log("WS STATUS:", status);
      }
    );
  
    return () => disconnectUserSocket();
  }, [wallet?.userId]);  
  
  useAlertStomp((alert) => {
    console.log("📥 WALLET ALERT:", alert);
  
    if (!alert?.type) return;
  
    if (alert.type === "WALLET_UPDATED") {
      loadWallet();
    }
  
    if (alert.type === "FRAUD_ALERT") {
      setFraudWarning(true);
      toast.error("🚨 Suspicious transfer detected!");

      setTimeout(() => {
        setFraudWarning(false);
      }, 10000);
    }    
  });   

  if (loading) {
    return <div className="text-white/70">Loading wallet…</div>;
  }

  if (!wallet) return null;

  const usedToday =
    history
      .filter((h) => h.type === "TRANSFER_OUT" && h.status === "SUCCESS")
      .reduce((sum, h) => sum + Math.abs(Number(h.amount)), 0) || 0;

  const remainingLimit =
    Number(wallet.dailyLimit) - usedToday;

  const percent =
    Math.min(
      (usedToday / Number(wallet.dailyLimit)) * 100,
      100
    ) || 0;

    const sentToday =
  history
    .filter(h => h.type === "TRANSFER_OUT" && h.status === "SUCCESS")
    .reduce((sum, h) => sum + Math.abs(Number(h.amount)), 0) || 0;

const receivedToday =
  history
    .filter(h => h.type === "TRANSFER_IN" && h.status === "SUCCESS")
    .reduce((sum, h) => sum + Number(h.amount), 0) || 0;

  return (
    <div className="space-y-8">
      {/* HEADER */}
      <motion.div
        initial={{ opacity: 0, y: 12 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.35 }}
      >
        <div className="text-2xl font-extrabold">
          Banking <span className="text-lime-200">Wallet</span>
        </div>
        <div className="text-sm text-white/60">
          Enterprise wallet system with transfer protection
        </div>
      </motion.div>

      {fraudWarning && (
        <div className="rounded-2xl bg-red-500/10 border border-red-400/30 p-4 text-red-300 text-sm">
          🚨 Suspicious activity detected on your wallet.
          Please review recent transfers.
        </div>
      )}
      {/* WALLET SUMMARY */}
      <motion.div
        initial={{ opacity: 0, y: 15 }}
        animate={{ opacity: 1, y: 0 }}
        className="rounded-3xl bg-white/10 backdrop-blur-xl p-6 ring-1 ring-white/15"
      >
        <div className="flex justify-between items-start">
          <div>
            <div className="text-sm text-white/60">Available Balance</div>
            <div className="text-3xl font-extrabold text-lime-200 mt-1">
              ৳{Number(wallet.balance).toLocaleString()}
            </div>
          </div>

          <div className="flex gap-3">
            <button
              onClick={() => setTransferOpen(true)}
              className="rounded-2xl bg-lime-300 px-5 py-2 font-semibold text-[#061a12] hover:bg-lime-200 transition"
            >
              Transfer
            </button>

            <button
              onClick={() => setTopUpOpen(true)}
              className="rounded-2xl bg-white/10 px-5 py-2 font-semibold ring-1 ring-white/15 hover:bg-white/15 transition"
            >
              Top Up
            </button>
            <button
              onClick={() => setScanOpen(true)}
              className="rounded-2xl bg-white/10 px-5 py-2 font-semibold ring-1 ring-white/15 hover:bg-white/15 transition"
            >
              Scan QR
            </button>
            <button
              onClick={() => setQrGenerateOpen(true)}
              className="rounded-2xl bg-white/10 px-5 py-2 font-semibold ring-1 ring-white/15 hover:bg-white/15 transition"
            >
              Generate QR
            </button>
            <button
              onClick={() => setConnectOpen(true)}
              className="rounded-2xl bg-white/10 px-5 py-2 font-semibold ring-1 ring-white/15 hover:bg-white/15 transition"
            >
              + Connect Bank
            </button>
            <button
              onClick={() => setBanksModalOpen(true)}
              className="rounded-2xl bg-white/10 px-5 py-2 font-semibold ring-1 ring-white/15 hover:bg-white/15 transition"
            >
              Connected Banks ({banks.length})
            </button>
          </div>
        </div>

        {/* DAILY LIMIT BAR */}
        <div className="mt-6">
          <div className="flex justify-between text-xs text-white/60">
            <span>Daily Limit ৳{wallet.dailyLimit}</span>
            <span>
              Remaining ৳{remainingLimit.toLocaleString()}
            </span>
          </div>

          <div className="mt-2 h-3 rounded-full bg-white/10 overflow-hidden">
            <div
              className="h-full bg-lime-300 transition-all"
              style={{ width: `${percent}%` }}
            />
          </div>
        </div>

        <div className="mt-6 grid grid-cols-2 gap-4">
          <div className="rounded-2xl bg-white/10 p-4 ring-1 ring-white/15">
            <div className="text-xs text-white/60">Sent Today</div>
            <div className="text-lg font-bold text-red-300">
              ৳{sentToday.toLocaleString()}
            </div>
          </div>

          <div className="rounded-2xl bg-white/10 p-4 ring-1 ring-white/15">
            <div className="text-xs text-white/60">Received Today</div>
            <div className="text-lg font-bold text-lime-300">
              ৳{receivedToday.toLocaleString()}
            </div>
          </div>
        </div>
      </motion.div>

      {/* HISTORY */}
      <WalletHistoryTable history={history} />

      {/* TRANSFER MODAL */}
      <TransferModal
        open={transferOpen}
        onClose={() => setTransferOpen(false)}
        onSuccess={() => {
          loadWallet();
          toast.success("Transfer successful");
        }}
      />
      <TopUpModal
        open={topUpOpen}
        onClose={() => setTopUpOpen(false)}
        onSuccess={loadWallet}
      />
      <ConnectBankModal
        open={connectOpen}
        onClose={() => setConnectOpen(false)}
        onSuccess={() => {
          loadBanks();
          toast.success("Bank list updated");
        }}
      />
      <QrScannerModal
        open={scanOpen}
        onClose={() => setScanOpen(false)}
        onSuccess={loadWallet}
      />
      <QrGenerateModal
        open={qrGenerateOpen}
        onClose={() => setQrGenerateOpen(false)}
      />
      <BankManagementModal
        open={banksModalOpen}
        onClose={() => setBanksModalOpen(false)}
      />
    </div>
  );
}
