import { apiRequest } from "../lib/api";

export const familyApi = {
    getSummary(familyId, month, year) {
        return apiRequest(
          `/api/family/${familyId}/budget/summary?month=${month}&year=${year}`
        ).then(res => res.data);
      },      

  getMembers(familyId) {
    return apiRequest(`/api/family/${familyId}/members`)
      .then(res => res.data);
  },

  getCategories(familyId) {
    return apiRequest(
      `/api/family/${familyId}/budget/categories`
    );
  },

  planCategory(familyId, payload) {
    return apiRequest(
      `/api/family/${familyId}/budget/plan`,
      { method: "POST", body: payload }
    );
  },
  // getCategories(familyId, month, year) {
  //   return apiRequest(
  //     `/api/family/${familyId}/budget/items?month=${month}&year=${year}`
  //   ).then(res => res.data);
  // },

  sendInvite(familyId, payload) {
    return apiRequest(
      `/api/family/${familyId}/invite`,
      { method: "POST", body: payload }
    );
  },
  getMyFamilies() {
    return apiRequest("/api/family/my")
      .then(res => res.data);
  },
  
  createFamily(payload) {
    return apiRequest("/api/family/create", {
      method: "POST",
      body: payload,
    }).then(res => res.data);
  },  
  getMyInvitations() {
    return apiRequest("/api/family/invitations")
      .then(res => res.data);
  },
  
  acceptInvitation(token) {
    return apiRequest(
      `/api/family/invitations/accept?token=${token}`,
      { method: "POST" }
    );
  },
  
  rejectInvitation(token) {
    return apiRequest(
      `/api/family/invitations/reject?token=${token}`,
      { method: "POST" }
    );
  },  
  inviteMember(familyId, payload) {
    // payload: { email, role }
    return apiRequest(`/api/family/${familyId}/invite`, {
      method: "POST",
      body: payload,
    }).then((res) => res.data);
  },
  changeRole(familyId, userId, role) {
    return apiRequest(
      `/api/family/${familyId}/members/${userId}/role`,
      {
        method: "PUT",
        body: { role },
      }
    ).then(res => res.data);
  },  
  leaveFamily(familyId) {
    return apiRequest(
      `/api/family/${familyId}/leave`,
      { method: "DELETE" }
    ).then(res => res.data);
  },  
  createCategory(familyId, payload) {
    return apiRequest(
      `/api/family/${familyId}/budget/categories`,
      { method: "POST", body: payload }
    ).then(res => res.data);
  },  
  setIncome(familyId, payload) {
    return apiRequest(
      `/api/family/${familyId}/budget/income`,
      { method: "POST", body: payload }
    );
  },  
  getBudgetItems(familyId, month, year) {
    return apiRequest(
      `/api/family/${familyId}/budget/items?month=${month}&year=${year}`
    );
  }, 
  deleteCategory(familyId, categoryId) {
    return apiRequest(
      `/api/family/${familyId}/budget/categories/${categoryId}`,
      { method: "DELETE" }
    );
  },
  
  renameCategory(familyId, categoryId, name) {
    return apiRequest(
      `/api/family/${familyId}/budget/categories/${categoryId}`,
      {
        method: "PUT",
        body: { name }
      }
    );
  },
  spend(familyId, payload) {
    return apiRequest(
      `/api/family/${familyId}/budget/spend`,
      { method: "POST", body: payload }
    );
  },  
  getExpenses(familyId) {
    return apiRequest(
      `/api/family/${familyId}/budget/expenses`
    );
  },  
  getExpenseSummary(familyId, month, year) {
    return apiRequest(
      `/api/family/${familyId}/budget/expenses/summary?month=${month}&year=${year}`
    );
  },  
  
};