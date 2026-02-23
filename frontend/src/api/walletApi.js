import { apiRequest } from "../lib/api";

export const walletApi = {
    async getWallet() {
      return apiRequest("/api/wallet");
    },
  
    async getHistory(limit = 20) {
      return apiRequest(`/api/wallet/history?limit=${limit}`);
    },
  
    async transfer(payload) {
      return apiRequest("/api/wallet/transfer", {
        method: "POST",
        body: payload,
      });
    },
  
    async moveToBudget(amount) {
      return apiRequest(`/api/wallet/move-to-budget?amount=${amount}`, {
        method: "POST",
      });
    },

    async generateQr(payload) {
      return apiRequest("/api/wallet/qr/generate", {
        method: "POST",
        body: payload,
      });
    },
    
    async payQr(payload) {
      return apiRequest("/api/wallet/qr/pay", {
        method: "POST",
        body: payload,
      });
    },    
  };
  
