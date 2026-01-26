import { apiRequest } from "../../../lib/api";

export const familyBudgetApi = {
  summary(familyId, month, year) {
    return apiRequest(
      `/api/family/${familyId}/budget/summary?month=${month}&year=${year}`
    ).then(r => r.data);
  },

  categories(familyId) {
    return apiRequest(
      `/api/family/${familyId}/budget/categories`
    ).then(r => r.data);
  },

  setIncome(familyId, payload) {
    return apiRequest(
      `/api/family/${familyId}/budget/income`,
      { method: "POST", body: payload }
    );
  },

  plan(familyId, payload) {
    return apiRequest(
      `/api/family/${familyId}/budget/plan`,
      { method: "POST", body: payload }
    );
  },
};
