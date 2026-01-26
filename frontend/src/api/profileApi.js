import { apiRequest } from "../lib/api";

export const profileApi = {
  overview(month, year) {
    return apiRequest(
      `/api/profile/overview?month=${month}&year=${year}`
    ).then(res => res.data);
  },
};
