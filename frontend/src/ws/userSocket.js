import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

let client = null;

export function connectUserSocket(userId, onEvent, onStatus) {
  if (client && client.active) {
    console.log("⚠️ WS already connected");
    return;
  }

  const token = localStorage.getItem("token");

  client = new Client({
    webSocketFactory: () =>
      new SockJS(`http://localhost:8080/ws?token=${token}`),

    reconnectDelay: 5000,

    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,

    onConnect: () => {
      console.log("✅ WS CONNECTED AS USER", userId);
      onStatus?.("CONNECTED");

      // 🔔 Private alerts (wallet updates, fraud alerts)
      client.subscribe("/user/queue/alerts", (msg) => {
        const event = JSON.parse(msg.body);
        console.log("📩 ALERT:", event);
        onEvent(event);
      });

      // 🏦 Bank updates
      client.subscribe(
        `/topic/bank-updates/${userId}`,
        (msg) => {
          console.log("🏦 BANK UPDATE:", msg.body);
          onEvent({
            type: "BANK_UPDATE",
            message: msg.body,
          });
        }
      );
    },

    onStompError: (frame) => {
      console.error("❌ STOMP ERROR:", frame);
    },

    onWebSocketClose: () => {
      console.warn("⚠️ WS CLOSED");
      onStatus?.("DISCONNECTED");
    },
  });

  client.activate();
}

export function disconnectUserSocket() {
  if (client) {
    client.deactivate();
    client = null;
  }
}
