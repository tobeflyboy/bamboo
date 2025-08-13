import { defineStore } from "pinia";
import piniaPersistConfig from "@/stores/helper/persist";

export const useUserStore = defineStore({
  id: "bamboo-user",
  state: () => ({
    token: "",
    onlineUser: { realName: "Bamboo" }
  }),
  getters: {},
  actions: {
    // Set Token
    setToken(token) {
      this.token = token;
    },
    // Set setUserInfo
    setOnlineUser(onlineUser) {
      console.log("setOnlineUser:", onlineUser);
      this.onlineUser = onlineUser;
    }
  },
  persist: piniaPersistConfig("bamboo-user")
});
