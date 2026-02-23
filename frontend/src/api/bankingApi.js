// frontend/src/api/bankingApi.js
import { apiRequest } from "../lib/api";

export const bankingApi = {
  async getBanks() {
    const res = await apiRequest("/api/banking");
    return res?.data ?? [];  // <-- unwrap ApiResponse
  },

  async connect(payload) {
    // payload: { provider, accountNumber, initialBalance }
    const res = await apiRequest("/api/banking/connect", {
      method: "POST",
      body: payload,
    });
    return res;
  },

  async topUpFromBank(payload) {
    return apiRequest("/api/banking/topup", {
      method: "POST",
      body: {
        bankAccountId: String(payload.bankAccountId),
        amount: String(payload.amount)
      }
    });
  },  

  async getBankTransactions() {
    const res = await apiRequest("/api/banking/transactions");
    return res?.data ?? [];
  },
  
  async withdraw(payload) {
    return apiRequest("/api/banking/withdraw", {
      method: "POST",
      body: payload,
    });
  },
  
  async transferBetweenBanks(payload) {
    return apiRequest("/api/banking/transfer", {
      method: "POST",
      body: payload,
    });
  },
  
  async sync() {
    return apiRequest("/api/banking/sync", {
      method: "POST",
    });
  },
  
  async createMockBank(payload) {
    return apiRequest("/api/banking/mock/create", {
      method: "POST",
      body: payload
    });
  }  
};
