import { apiRequest } from "../../../lib/api";

export const recurringBillApi = {
  list() {
    return apiRequest("/api/recurring-bills");
  },

  create(payload) {
    return apiRequest("/api/recurring-bills", {
      method: "POST",
      body: payload,
      headers: {
        "Idempotency-Key": crypto.randomUUID(),
      },
    });
  },

  toggle(id) {
    return apiRequest(`/api/recurring-bills/${id}/toggle`, {
      method: "PUT",
      headers: {
        "Idempotency-Key": crypto.randomUUID(),
      },
    });
  },

  remove(id) {
    return apiRequest(`/api/recurring-bills/${id}`, {
      method: "DELETE",
      headers: {
        "Idempotency-Key": crypto.randomUUID(),
      },
    });
  },
};
